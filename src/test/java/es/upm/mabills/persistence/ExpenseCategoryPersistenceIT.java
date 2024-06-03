package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import es.upm.mabills.exceptions.ExpenseCategoryAlreadyExistsException;
import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class ExpenseCategoryPersistenceIT {
    private static final String USERNAME = "username";
    private static final String OTHER_USER = "otherUser";
    private static final String TO_UPDATE_EXPENSE_CATEGORY_USER = "toUpdateExpenseCategoryUser";
    private static final String TO_DELETE_EXPENSE_CATEGORY = "toDeleteExpenseCategory";
    private static final String TO_DELETE_EXPENSE_CATEGORY_WITH_EXPENSE_RESOURCE = "toDeleteExpenseCategoryWithExpenseResource";

    private static final String USERNAME_USER_EXPENSE_CATEGORY = "userNameUserExpenseCategory";
    private static final String NEW_EXPENSE_CATEGORY = "newExpenseCategory";
    private static final String NOT_FOUND_USER = "notFoundUser";
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final UUID RANDOM_UUID = UUID.randomUUID();

    @Autowired
    private ExpenseCategoryPersistence expenseCategoryPersistence;

    @Test
    void testFindExpenseCategoryByUser() {
        List<ExpenseCategoryEntity> expenseCategories = expenseCategoryPersistence.findExpenseCategoryByUserName(USERNAME);
        assertEquals(1, expenseCategories.size());
        assertEquals(USERNAME_USER_EXPENSE_CATEGORY, expenseCategories.get(0).getName());
    }

    @Test
    void testFindExpenseCategoryByUserNotFound() {
        assertTrue(expenseCategoryPersistence.findExpenseCategoryByUserName("notFound").isEmpty());
    }

    @Test
    void testCreateExpenseCategory() {
        ExpenseCategory expenseCategory = ExpenseCategory.builder()
                .name(NEW_EXPENSE_CATEGORY)
                .build();
        ExpenseCategoryEntity createdExpenseCategory = expenseCategoryPersistence.createExpenseCategory(OTHER_USER, expenseCategory);
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
        ExpenseCategoryEntity newExpenseCategory = expenseCategoryPersistence.createExpenseCategory(TO_UPDATE_EXPENSE_CATEGORY_USER,
                ExpenseCategory.builder().name(TO_UPDATE_EXPENSE_CATEGORY_USER).build());
        ExpenseCategoryEntity updatedExpenseCategory = expenseCategoryPersistence.updateExpenseCategoryName(TO_UPDATE_EXPENSE_CATEGORY_USER,
                newExpenseCategory.getUuid(), NEW_EXPENSE_CATEGORY);
        assertEquals(NEW_EXPENSE_CATEGORY, updatedExpenseCategory.getName());
    }

    @Test
    void testUpdateExpenseCategoryNameUserNotFound() {
        assertThrows(ExpenseCategoryNotFoundException.class, () -> expenseCategoryPersistence.updateExpenseCategoryName(NOT_FOUND_USER,
                RANDOM_UUID, NEW_EXPENSE_CATEGORY));
    }

    @Test
    void testUpdateExpenseCategoryNameNotFound() {
        assertThrows(ExpenseCategoryNotFoundException.class, () -> expenseCategoryPersistence.updateExpenseCategoryName(TO_UPDATE_EXPENSE_CATEGORY_USER,
                RANDOM_UUID, NEW_EXPENSE_CATEGORY));
    }

    @Test
    void testDeleteExpenseCategory() {
        ExpenseCategoryEntity newExpenseCategory = expenseCategoryPersistence.createExpenseCategory(TO_UPDATE_EXPENSE_CATEGORY_USER,
                ExpenseCategory.builder().name(TO_DELETE_EXPENSE_CATEGORY).build());
        assertDoesNotThrow(() -> expenseCategoryPersistence.deleteExpenseCategory(TO_UPDATE_EXPENSE_CATEGORY_USER, newExpenseCategory.getUuid()));
        UUID newExpenseCategoryUuid = newExpenseCategory.getUuid();
        assertThrows(ExpenseCategoryNotFoundException.class, () -> expenseCategoryPersistence.deleteExpenseCategory(TO_UPDATE_EXPENSE_CATEGORY_USER, newExpenseCategoryUuid));
    }

    @Test
    void testDeleteExpenseCategoryUserNotFound() {
        assertThrows(ExpenseCategoryNotFoundException.class, () -> expenseCategoryPersistence.deleteExpenseCategory(NOT_FOUND_USER, RANDOM_UUID));
    }

    @Test
    void testDeleteExpenseCategoryNotFound() {
        assertThrows(ExpenseCategoryNotFoundException.class, () -> expenseCategoryPersistence.deleteExpenseCategory(TO_UPDATE_EXPENSE_CATEGORY_USER, RANDOM_UUID));
    }

    @Test
    void testDeleteExpenseCategoryWithDependentEntities() {
        assertDoesNotThrow(() -> expenseCategoryPersistence.deleteExpenseCategory(ENCODED_PASSWORD_USER, findExpenseCategoryUuid()));
    }

    private UUID findExpenseCategoryUuid() {
        return expenseCategoryPersistence.findExpenseCategoryByUserName(ENCODED_PASSWORD_USER)
                .stream()
                .filter(expenseCategoryEntity -> expenseCategoryEntity.getName().equals(ExpenseCategoryPersistenceIT.TO_DELETE_EXPENSE_CATEGORY_WITH_EXPENSE_RESOURCE))
                .findFirst()
                .map(ExpenseCategoryEntity::getUuid)
                .orElse(null);
    }
}
