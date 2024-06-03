package es.upm.mabills.persistence.entity_dependent_managers;

import es.upm.mabills.persistence.repositories.CreditCardRepository;
import es.upm.mabills.persistence.repositories.ExpenseRepository;
import es.upm.mabills.persistence.repositories.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Qualifier("bankAccountEntityRelationshipsManager")
public class BankAccountEntityDependentManager implements EntityDependentManager {
    private final CreditCardRepository creditCardRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    @Autowired
    public BankAccountEntityDependentManager(CreditCardRepository creditCardRepository, ExpenseRepository expenseRepository,
                                             IncomeRepository incomeRepository) {
        this.creditCardRepository = creditCardRepository;
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
    }

    @Override
    public <T> void decouple(T id) {
        creditCardRepository.decoupleBankAccount((UUID) id);
        expenseRepository.decoupleBankAccount((UUID) id);
        incomeRepository.decoupleBankAccount((UUID) id);
    }
}
