package es.upm.mabills.persistence.entity_dependent_managers;

import es.upm.mabills.persistence.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("expenseCategoryEntityDependentManager")
public class ExpenseCategoryEntityDependentManager implements EntityDependentManager {
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseCategoryEntityDependentManager(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public <T> void decouple(T id) {
        expenseRepository.decoupleExpenseCategory((Integer) id);
    }
}
