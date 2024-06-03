package es.upm.mabills.persistence;

import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.repositories.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class BankAccountReferenceBuilder {
    private final EntityReferenceFactory entityReferenceFactory;
    private final CreditCardRepository creditCardRepository;

    @Autowired
    public BankAccountReferenceBuilder(EntityReferenceFactory entityReferenceFactory, CreditCardRepository creditCardRepository) {
        this.entityReferenceFactory = entityReferenceFactory;
        this.creditCardRepository = creditCardRepository;
    }

    public BankAccountEntity buildBankAccountEntityReference(BankAccount bankAccount, CreditCard creditCard, UserPrincipal userPrincipal) {
        return Optional.ofNullable(bankAccount)
                .map(BankAccount::getUuid)
                .map(UUID::fromString)
                .map(uuid -> entityReferenceFactory.buildReference(BankAccountEntity.class, uuid))
                .orElse(buildBankAccountEntityReferenceFromCreditCard(userPrincipal, creditCard));
    }

    private BankAccountEntity buildBankAccountEntityReferenceFromCreditCard(UserPrincipal userPrincipal, CreditCard creditCard) {
        return Optional.ofNullable(creditCard)
                .map(creditcard -> creditCardRepository.findByUserIdAndUuid(userPrincipal.getId(), UUID.fromString(creditCard.getUuid())))
                .map(CreditCardEntity::getBankAccount)
                .map(BankAccountEntity::getUuid)
                .map(uuid -> entityReferenceFactory.buildReference(BankAccountEntity.class, uuid))
                .orElse(null);
    }
}
