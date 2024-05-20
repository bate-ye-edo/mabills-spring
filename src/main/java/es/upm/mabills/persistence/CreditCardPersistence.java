package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.CreditCardAlreadyExistsException;
import es.upm.mabills.exceptions.CreditCardNotFoundException;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.entity_decouplers.EntityDependentManager;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
import es.upm.mabills.persistence.repositories.CreditCardRepository;
import es.upm.mabills.persistence.repositories.RepositorySort;
import es.upm.mabills.persistence.repositories.UserRepository;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CreditCardPersistence {
    private final CreditCardRepository creditCardRepository;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final EntityDependentManager entityDependentManager;
    @Autowired
    public CreditCardPersistence(CreditCardRepository creditCardRepository, UserRepository userRepository,
                                 BankAccountRepository bankAccountRepository, @Qualifier("creditCardEntityDependentManager") EntityDependentManager entityDependentManager) {
        this.creditCardRepository = creditCardRepository;
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.entityDependentManager = entityDependentManager;
    }

    public List<CreditCardEntity> findCreditCardsForUser(UserPrincipal user) {
        return creditCardRepository.findByUserId(user.getId(), RepositorySort.BY_CREATION_DATE.value());
    }

    public CreditCardEntity createCreditCard(UserPrincipal user, CreditCard creditCard) {
        assertCreditCardNotExistsForUser(user, creditCard);
        return Try.of(() -> userRepository.getReferenceById(user.getId()))
                .map(userEntity -> buildCreditCard(userEntity, creditCard))
                .map(creditCardRepository::save)
                .get();
    }

    private void assertCreditCardNotExistsForUser(UserPrincipal user, CreditCard creditCard) {
        if(creditCardRepository.findByUserIdAndCreditCardNumber(user.getId(), creditCard.getCreditCardNumber()) != null) {
            throw new CreditCardAlreadyExistsException(user.getUsername(), creditCard.getCreditCardNumber());
        }
    }

    private CreditCardEntity buildCreditCard(UserEntity userEntity, CreditCard creditCard) {
        BankAccountEntity bankAccountEntity = null;
        if(creditCard.getBankAccount() != null) {
            bankAccountEntity = bankAccountRepository.getReferenceById(UUID.fromString(creditCard.getBankAccount().getUuid()));
        }
        return CreditCardEntity.builder()
                .user(userEntity)
                .bankAccount(bankAccountEntity)
                .creditCardNumber(creditCard.getCreditCardNumber())
                .build();
    }

    public void deleteCreditCard(UserPrincipal userPrincipal, String uuid) {
        Try.of(() -> creditCardRepository.findByUserIdAndUuid(userPrincipal.getId(), UUID.fromString(uuid)))
                .andThen(this::deleteCreditCard)
                .onFailure(ex ->{
                   throw new CreditCardNotFoundException(uuid);
                });
    }

    public void deleteCreditCard(CreditCardEntity creditCardEntity) {
        this.entityDependentManager.decouple(creditCardEntity.getUuid());
        this.creditCardRepository.delete(creditCardEntity);
    }
}
