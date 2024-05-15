package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.CreditCardAlreadyExistsException;
import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.exception_mappers.CreditCardDataIntegrityExceptionMapper;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
import es.upm.mabills.persistence.repositories.CreditCardRepository;
import es.upm.mabills.persistence.repositories.UserRepository;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CreditCardPersistence {
    private final CreditCardRepository creditCardRepository;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public CreditCardPersistence(CreditCardRepository creditCardRepository, UserRepository userRepository,
                                 BankAccountRepository bankAccountRepository) {
        this.creditCardRepository = creditCardRepository;
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    public List<CreditCardEntity> findCreditCardsForUser(UserPrincipal user) {
        return Try.of(()->creditCardRepository.findByUserId(user.getId()))
                .getOrElseThrow(MaBillsServiceException::new);
    }

    public CreditCardEntity createCreditCard(UserPrincipal user, CreditCard creditCard) {
        assertCreditCardNotExistsForUser(user, creditCard);
        return Try.of(() -> userRepository.getReferenceById(user.getId()))
                .map(userEntity -> buildCreditCard(userEntity, creditCard))
                .map(creditCardRepository::save)
                .getOrElseThrow(e -> CreditCardDataIntegrityExceptionMapper.map(e, user.getUsername(), creditCard));
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
}
