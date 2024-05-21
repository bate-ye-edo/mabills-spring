package es.upm.mabills.persistence;

import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.persistence.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExpensePersistence {
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpensePersistence(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<ExpenseEntity> findExpenseByUserId(UserPrincipal userPrincipal) {
        return expenseRepository.findByUserId(userPrincipal.getId());
    }
}
