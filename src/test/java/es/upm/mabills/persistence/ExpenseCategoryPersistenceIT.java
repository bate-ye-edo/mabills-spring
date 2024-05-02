package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import es.upm.mabills.exceptions.ExpenseCategoryAlreadyExistsException;
import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.model.ExpenseCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestConfig
class ExpenseCategoryPersistenceIT {
    private static final String USERNAME = "username";
    private static final String OTHER_USER = "otherUser";
    private static final String TO_UPDATE_EXPENSE_CATEGORY_USER = "toUpdateExpenseCategoryUser";

    private static final String USERNAME_USER_EXPENSE_CATEGORY = "userNameUserExpenseCategory";
    private static final String NEW_EXPENSE_CATEGORY = "newExpenseCategory";
    private static final String NOT_FOUND_USER = "notFoundUser";
    private static final UUID RANDOM_UUID = UUID.randomUUID();

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

    @Test
    void testCreateExpenseCategory() {
        ExpenseCategory expenseCategory = ExpenseCategory.builder()
                .name(NEW_EXPENSE_CATEGORY)
                .build();
        ExpenseCategory createdExpenseCategory = expenseCategoryPersistence.createExpenseCategory(OTHER_USER, expenseCategory);
        assertEquals(expenseCategory.getName(), createdExpenseCategory.getName());
    }

    @Test
    void testCreateExpenseCategoryAlreadyExists() {
        ExpenseCategory expenseCategory = ExpenseCategory.builder()
                .name(USERNAME_USER_EXPENSE_CATEGORY)
                .build();
        assertThrows(ExpenseCategoryAlreadyExistsException.class, () -> expenseCategoryPersistence.createExpenseCategory(USERNAME, expenseCategory));
    }

    @Test
    void testCreateExpenseCategoryUserNotFound() {
        ExpenseCategory expenseCategory = ExpenseCategory.builder()
                .name(NEW_EXPENSE_CATEGORY)
                .build();
        assertThrows(UserNotFoundException.class, () -> expenseCategoryPersistence.createExpenseCategory(NOT_FOUND_USER, expenseCategory));
    }

    @Test
    void testUpdateExpenseCategoryName() {
        ExpenseCategory newExpenseCategory = expenseCategoryPersistence.createExpenseCategory(TO_UPDATE_EXPENSE_CATEGORY_USER, ExpenseCategory.builder().name(TO_UPDATE_EXPENSE_CATEGORY_USER).build());
        ExpenseCategory updatedExpenseCategory = expenseCategoryPersistence.updateExpenseCategoryName(TO_UPDATE_EXPENSE_CATEGORY_USER, UUID.fromString(newExpenseCategory.getUuid()), NEW_EXPENSE_CATEGORY);
        assertEquals(NEW_EXPENSE_CATEGORY, updatedExpenseCategory.getName());
    }

    @Test
    void testUpdateExpenseCategoryNameUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> expenseCategoryPersistence.updateExpenseCategoryName(NOT_FOUND_USER, RANDOM_UUID, NEW_EXPENSE_CATEGORY));
    }

    @Test
    void testUpdateExpenseCategoryNameNotFound() {
        assertThrows(ExpenseCategoryNotFoundException.class, () -> expenseCategoryPersistence.updateExpenseCategoryName(TO_UPDATE_EXPENSE_CATEGORY_USER, RANDOM_UUID, NEW_EXPENSE_CATEGORY));
    }
}
