package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.model.filters.Filter;
import es.upm.mabills.model.filters.FilterComparisons;
import es.upm.mabills.model.filters.FilterField;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class FilterPersistenceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String EXIST_CREDIT_CARD = "004120003120034012";
    private static final String EXIST_CREDIT_CARD_PART = "412";
    private static final String NOT_EXIST_CREDIT_CARD = "XXXXXXXXXXXXXXXXX";
    private static final String NOT_EXIST_CREDIT_CARD_PART = "XXX";

    @Autowired
    private FilterPersistence filterPersistence;

    @Autowired
    private UserRepository userRepository;

    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        UserEntity userEntity = userRepository.findByUsername(ENCODED_PASSWORD_USER);
        userPrincipal = UserPrincipal.builder()
            .id(userEntity.getId())
            .username(userEntity.getUsername())
            .build();
    }

    @Test
    void testApplyFiltersExpensesAmountEquals() {
        Filter expenseAmountFilter = Filter.builder()
            .filterField(FilterField.AMOUNT)
            .filterComparison(FilterComparisons.EQUAL)
            .filterValue(BigDecimal.ONE.toString())
            .build();
        List<ExpenseEntity> expenses = filterPersistence.applyFilters(List.of(expenseAmountFilter), ExpenseEntity.class, userPrincipal);
        assertNotNull(expenses);
        assertFalse(expenses.isEmpty());
    }

    @Test
    void testApplyFiltersExpensesAmountGreaterThan() {
        Filter expenseAmountFilter = Filter.builder()
            .filterField(FilterField.AMOUNT)
            .filterComparison(FilterComparisons.GREATER_THAN)
            .filterValue(BigDecimal.ONE.toString())
            .build();
        List<ExpenseEntity> expenses = filterPersistence.applyFilters(List.of(expenseAmountFilter), ExpenseEntity.class, userPrincipal);
        assertNotNull(expenses);
        assertFalse(expenses.isEmpty());
    }

    @Test
    void testApplyFiltersExpensesAmountLessThan() {
        Filter expenseAmountFilter = Filter.builder()
            .filterField(FilterField.AMOUNT)
            .filterComparison(FilterComparisons.LESS_THAN)
            .filterValue(BigDecimal.TEN.toString())
            .build();
        List<ExpenseEntity> expenses = filterPersistence.applyFilters(List.of(expenseAmountFilter), ExpenseEntity.class, userPrincipal);
        assertNotNull(expenses);
        assertFalse(expenses.isEmpty());
    }

    @Test
    void testApplyFiltersExpensesAmountBetween() {
        Filter expenseAmountFilter = Filter.builder()
            .filterField(FilterField.AMOUNT)
            .filterComparison(FilterComparisons.BETWEEN)
            .filterValue(BigDecimal.ONE.toString())
            .secondFilterValue(BigDecimal.TEN.toString())
            .build();
        List<ExpenseEntity> expenses = filterPersistence.applyFilters(List.of(expenseAmountFilter), ExpenseEntity.class, userPrincipal);
        assertNotNull(expenses);
        assertFalse(expenses.isEmpty());
    }

    @Test
    void testApplyFiltersExpensesAmountNotEqual() {
        Filter expenseAmountFilter = Filter.builder()
            .filterField(FilterField.AMOUNT)
            .filterComparison(FilterComparisons.NOT_EQUAL)
            .filterValue(BigDecimal.ONE.toString())
            .build();
        List<ExpenseEntity> expenses = filterPersistence.applyFilters(List.of(expenseAmountFilter), ExpenseEntity.class, userPrincipal);
        assertNotNull(expenses);
        assertFalse(expenses.isEmpty());
    }

    @Test
    void testApplyFiltersExpensesAmountGreaterThanOrEqual() {
        Filter expenseAmountFilter = Filter.builder()
            .filterField(FilterField.AMOUNT)
            .filterComparison(FilterComparisons.GREATER_THAN_OR_EQUAL)
            .filterValue(BigDecimal.ONE.toString())
            .build();
        List<ExpenseEntity> expenses = filterPersistence.applyFilters(List.of(expenseAmountFilter), ExpenseEntity.class, userPrincipal);
        assertNotNull(expenses);
        assertFalse(expenses.isEmpty());
    }

    @Test
    void testApplyFiltersExpensesAmountLessThanOrEqual() {
        Filter expenseAmountFilter = Filter.builder()
            .filterField(FilterField.AMOUNT)
            .filterComparison(FilterComparisons.LESS_THAN_OR_EQUAL)
            .filterValue(BigDecimal.TEN.toString())
            .build();
        List<ExpenseEntity> expenses = filterPersistence.applyFilters(List.of(expenseAmountFilter), ExpenseEntity.class, userPrincipal);
        assertNotNull(expenses);
        assertFalse(expenses.isEmpty());
    }

    @Test
    void testApplyFiltersExpensesAmountEqualsEmpty() {
        Filter expenseAmountFilter = Filter.builder()
                .filterField(FilterField.AMOUNT)
                .filterComparison(FilterComparisons.EQUAL)
                .filterValue(BigDecimal.ZERO.toString())
                .build();
        List<ExpenseEntity> expenses = filterPersistence.applyFilters(List.of(expenseAmountFilter), ExpenseEntity.class, userPrincipal);
        assertNotNull(expenses);
        assertTrue(expenses.isEmpty());
    }

    @Test
    void testApplyFiltersExpensesCreditCardEquals() {
        Filter expenseCreditCardFilter = Filter.builder()
            .filterField(FilterField.CREDIT_CARD)
            .filterComparison(FilterComparisons.EQUAL)
            .filterValue(EXIST_CREDIT_CARD)
            .build();
        List<ExpenseEntity> expenses = filterPersistence.applyFilters(List.of(expenseCreditCardFilter), ExpenseEntity.class, userPrincipal);
        assertNotNull(expenses);
        assertFalse(expenses.isEmpty());
    }

    @Test
    void testApplyFiltersExpensesCreditCardContains() {
        Filter expenseCreditCardFilter = Filter.builder()
            .filterField(FilterField.CREDIT_CARD)
            .filterComparison(FilterComparisons.CONTAINS)
            .filterValue(EXIST_CREDIT_CARD_PART)
            .build();
        List<ExpenseEntity> expenses = filterPersistence.applyFilters(List.of(expenseCreditCardFilter), ExpenseEntity.class, userPrincipal);
        assertNotNull(expenses);
        assertFalse(expenses.isEmpty());
    }

    @Test
    void testApplyFiltersExpensesCreditCardEmpty() {
        Filter expenseCreditCardFilter = Filter.builder()
            .filterField(FilterField.CREDIT_CARD)
            .filterComparison(FilterComparisons.EQUAL)
            .filterValue(NOT_EXIST_CREDIT_CARD)
            .build();
        List<ExpenseEntity> expenses = filterPersistence.applyFilters(List.of(expenseCreditCardFilter), ExpenseEntity.class, userPrincipal);
        assertNotNull(expenses);
        assertTrue(expenses.isEmpty());
    }

    @Test
    void testApplyFiltersExpensesCreditCardContainsEmpty() {
        Filter expenseCreditCardFilter = Filter.builder()
            .filterField(FilterField.CREDIT_CARD)
            .filterComparison(FilterComparisons.CONTAINS)
            .filterValue(NOT_EXIST_CREDIT_CARD_PART)
            .build();
        List<ExpenseEntity> expenses = filterPersistence.applyFilters(List.of(expenseCreditCardFilter), ExpenseEntity.class, userPrincipal);
        assertNotNull(expenses);
        assertTrue(expenses.isEmpty());
    }

    @Test
    void testApplyFiltersExpensesCreditCardNotEqual() {
        Filter expenseCreditCardFilter = Filter.builder()
            .filterField(FilterField.CREDIT_CARD)
            .filterComparison(FilterComparisons.NOT_EQUAL)
            .filterValue(EXIST_CREDIT_CARD)
            .build();
        List<ExpenseEntity> expenses = filterPersistence.applyFilters(List.of(expenseCreditCardFilter), ExpenseEntity.class, userPrincipal);
        assertNotNull(expenses);
        assertFalse(expenses.isEmpty());
    }
}
