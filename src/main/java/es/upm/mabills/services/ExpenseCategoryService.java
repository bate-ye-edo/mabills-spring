package es.upm.mabills.services;

import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.persistence.ExpenseCategoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseCategoryService {
    private final ExpenseCategoryPersistence expenseCategoryPersistence;

    @Autowired
    public ExpenseCategoryService(ExpenseCategoryPersistence expenseCategoryPersistence) {
        this.expenseCategoryPersistence = expenseCategoryPersistence;
    }

    public List<ExpenseCategory> getExpenseCategoriesByUserName(String username) {
        return expenseCategoryPersistence.findExpenseCategoryByUserName(username);
    }

    public ExpenseCategory createExpenseCategory(String userName, ExpenseCategory expenseCategory) {
        return expenseCategoryPersistence.createExpenseCategory(userName, expenseCategory);
    }
}
