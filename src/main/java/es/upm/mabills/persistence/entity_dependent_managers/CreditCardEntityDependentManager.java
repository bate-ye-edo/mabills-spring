package es.upm.mabills.persistence.entity_dependent_managers;

import es.upm.mabills.persistence.repositories.ExpenseRepository;
import es.upm.mabills.persistence.repositories.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Qualifier("creditCardEntityDependentManager")
public class CreditCardEntityDependentManager implements EntityDependentManager {
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    @Autowired
    public CreditCardEntityDependentManager(ExpenseRepository expenseRepository, IncomeRepository incomeRepository) {
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
    }

    @Override
    public <T> void decouple(T id) {
        expenseRepository.decoupleCreditCard((UUID) id);
        incomeRepository.decoupleCreditCard((UUID) id);
    }
}
