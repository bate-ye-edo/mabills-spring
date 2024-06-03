package es.upm.mabills.persistence;

import es.upm.mabills.model.CreditCard;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CreditCardReferenceBuilder {
    private final EntityReferenceFactory entityReferenceFactory;

    @Autowired
    public CreditCardReferenceBuilder(EntityReferenceFactory entityReferenceFactory) {
        this.entityReferenceFactory = entityReferenceFactory;
    }

    public CreditCardEntity buildCreditCardEntityReference(CreditCard creditCard) {
        return Optional.ofNullable(creditCard)
                .map(CreditCard::getUuid)
                .map(UUID::fromString)
                .map(uuid -> entityReferenceFactory.buildReference(CreditCardEntity.class, uuid))
                .orElse(null);
    }
}
