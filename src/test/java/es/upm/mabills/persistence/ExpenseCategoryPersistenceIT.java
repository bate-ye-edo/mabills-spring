package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.model.ExpenseCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestConfig
class ExpenseCategoryPersistenceIT {
    private static final String USERNAME = "username";
    private static final String USERNAME_USER_EXPENSE_CATEGORY = "userNameUserExpenseCategory";
    @Autowired
    private ExpenseCategoryPersistence expenseCategoryPersistence;

    @Test
    void testFindExpenseCategoryByUser() {
        List<ExpenseCategory> expenseCategories = expenseCategoryPersistence.findExpenseCategoryByUserName(USERNAME);
        assertEquals(1, expenseCategories.size());
        assertEquals(USERNAME_USER_EXPENSE_CATEGORY, expenseCategories.get(0).getName());
    }

    @Test
    void testFindExpenseCategoryByUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> expenseCategoryPersistence.findExpenseCategoryByUserName("notFound"));
    }
}
