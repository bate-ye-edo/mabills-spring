package es.upm.mabills.persistence.entity_dependent_managers;

import es.upm.mabills.persistence.repositories.CreditCardRepository;
import es.upm.mabills.persistence.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Qualifier("bankAccountEntityRelationshipsManager")
public class BankAccountEntityDependentManager implements EntityDependentManager {
    private final CreditCardRepository creditCardRepository;
    private final ExpenseRepository expenseRepository;
    @Autowired
    public BankAccountEntityDependentManager(CreditCardRepository creditCardRepository, ExpenseRepository expenseRepository) {
        this.creditCardRepository = creditCardRepository;
        this.expenseRepository = expenseRepository;
    }

    @Override
    public <T> void decouple(T id) {
        creditCardRepository.decoupleBankAccount((UUID) id);
        expenseRepository.decoupleBankAccount((UUID) id);
    }
}
