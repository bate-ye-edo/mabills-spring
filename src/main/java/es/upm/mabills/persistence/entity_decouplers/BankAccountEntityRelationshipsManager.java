package es.upm.mabills.persistence.entity_decouplers;

import es.upm.mabills.persistence.repositories.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Qualifier("bankAccountEntityRelationshipsManager")
public class BankAccountEntityRelationshipsManager implements EntityRelationshipsManager {
    private final CreditCardRepository creditCardRepository;

    @Autowired
    public BankAccountEntityRelationshipsManager(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    @Override
    public <T> void decouple(T id) {
        creditCardRepository.decoupleBankAccount((UUID) id);
    }
}
