package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.CreditCardAlreadyExistsException;
import es.upm.mabills.exceptions.CreditCardNotFoundException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.entity_dependent_managers.EntityDependentManager;
import es.upm.mabills.persistence.repositories.CreditCardRepository;
import es.upm.mabills.persistence.repositories.RepositorySort;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CreditCardPersistence {
    private final CreditCardRepository creditCardRepository;
    private final EntityDependentManager entityDependentManager;
    private final EntityReferenceFactory entityReferenceFactory;

    @Autowired
    public CreditCardPersistence(CreditCardRepository creditCardRepository,
                                 @Qualifier("creditCardEntityDependentManager") EntityDependentManager entityDependentManager,
                                 EntityReferenceFactory entityReferenceFactory) {
        this.creditCardRepository = creditCardRepository;
        this.entityDependentManager = entityDependentManager;
        this.entityReferenceFactory = entityReferenceFactory;
    }

    public List<CreditCardEntity> findCreditCardsForUser(UserPrincipal user) {
        return creditCardRepository.findByUserId(user.getId(), RepositorySort.BY_CREATION_DATE.value());
    }

    public CreditCardEntity createCreditCard(UserPrincipal user, CreditCard creditCard) {
        assertCreditCardNotExistsForUser(user, creditCard);
        return Try.of(() -> entityReferenceFactory.buildReference(UserEntity.class, user.getId()))
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
        return CreditCardEntity.builder()
                .user(userEntity)
                .bankAccount(getBankAccountEntity(creditCard.getBankAccount()))
                .creditCardNumber(creditCard.getCreditCardNumber())
                .build();
    }

    private BankAccountEntity getBankAccountEntity(BankAccount bankAccount) {
        return Optional.ofNullable(bankAccount)
                .map(BankAccount::getUuid)
                .map(UUID::fromString)
                .map(uuid -> entityReferenceFactory.buildReference(BankAccountEntity.class, uuid))
                .orElse(null);
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
