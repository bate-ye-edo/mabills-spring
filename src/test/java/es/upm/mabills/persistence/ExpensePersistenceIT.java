package es.upm.mabills.persistence;


import es.upm.mabills.TestConfig;
import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.exceptions.ExpenseNotFoundException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.ChartData;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.model.FormOfPayment;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.chart_data_dtos.DateChartData;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class ExpensePersistenceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String TO_UPDATE_EXPENSE_CREDIT_CARD_NUMBER = "004120003120034012";
    private static final String ANOTHER_DESCRIPTION = "anotherDescription";
    private static final UserPrincipal NOT_FOUND_USER_PRINCIPAL = UserPrincipal.builder()
            .id(0)
            .username("notFoundUser")
            .build();
    private static final String RANDOM_UUID = UUID.randomUUID().toString();
    private static final String TO_DELETE_EXPENSE_DESCRIPTION = "to_delete_expense";

    @Autowired
    private ExpensePersistence expensePersistence;

    @Autowired
    private UserRepository userRepository;

    private UserPrincipal encodedUserPrincipal;

    private UserEntity encodedUserEntity;

    @BeforeEach
    void setUp() {
        encodedUserPrincipal = UserPrincipal.builder()
                .id(userRepository.findByUsername(ENCODED_PASSWORD_USER).getId())
                .username(ENCODED_PASSWORD_USER)
                .build();
        encodedUserEntity = userRepository.findByUsername(ENCODED_PASSWORD_USER);
    }

    @Test
    void testFindExpenseByUserId() {
        assertFalse(expensePersistence.findExpenseByUserId(encodedUserPrincipal).isEmpty());
    }

    @Test
    void testFindExpenseByUserIdNotFound() {
        assertTrue(expensePersistence.findExpenseByUserId(NOT_FOUND_USER_PRINCIPAL).isEmpty());
    }

    @Test
    void testCreateExpenseWithoutDependenciesSuccess() {
        Expense expense = buildExpenseWithoutDependencies();
        ExpenseEntity expenseEntity = expensePersistence.createExpense(encodedUserPrincipal, expense);
        assertNotNull(expenseEntity);
        assertEquals(expense.getAmount(), expenseEntity.getAmount());
        assertEquals(expense.getExpenseDate(), expenseEntity.getExpenseDate());
        assertEquals(expense.getDescription(), expenseEntity.getDescription());
        assertEquals(expense.getFormOfPayment().name(), expenseEntity.getFormOfPayment());
        assertNotNull(expenseEntity.getCreationDate());
    }

    @Test
    @Transactional
    void testCreateExpenseWithDependenciesSuccess() {
        Expense expense = buildExpenseWithDependencies();
        ExpenseEntity expenseEntity = expensePersistence.createExpense(encodedUserPrincipal, expense);
        assertNotNull(expenseEntity);
        assertEquals(expense.getAmount(), expenseEntity.getAmount());
        assertEquals(expense.getExpenseDate(), expenseEntity.getExpenseDate());
        assertEquals(expense.getDescription(), expenseEntity.getDescription());
        assertEquals(expense.getFormOfPayment().name(), expenseEntity.getFormOfPayment());
        assertNotNull(expenseEntity.getCreationDate());
        assertNotNull(expenseEntity.getBankAccount());
        assertNotNull(expenseEntity.getCreditCard());
        assertNotNull(expenseEntity.getExpenseCategory());
    }

    @Test
    void testUpdateExpenseSuccess() {
        Expense expense = buildExpenseToUpdate();
        ExpenseEntity updatedExpenseEntity = expensePersistence.updateExpense(encodedUserPrincipal, expense);
        assertNotNull(updatedExpenseEntity);
        assertEquals(expense.getAmount(), updatedExpenseEntity.getAmount());
        assertEquals(expense.getExpenseDate(), updatedExpenseEntity.getExpenseDate());
        assertEquals(expense.getDescription(), updatedExpenseEntity.getDescription());
        assertEquals(expense.getFormOfPayment().name(), updatedExpenseEntity.getFormOfPayment());
    }

    @Test
    void testUpdateExpenseExpenseNotFound() {
        Expense expense = Expense.builder().build();
        assertThrows(NullPointerException.class, () -> expensePersistence.updateExpense(encodedUserPrincipal, expense));
    }

    @Test
    void testUpdateExpenseExpenseCategoryNotFound() {
        Expense expense = buildExpenseToUpdateNotFoundExpenseCategory();
        assertThrows(ExpenseCategoryNotFoundException.class, () -> expensePersistence.updateExpense(encodedUserPrincipal, expense));
    }

    @Test
    void testUpdateExpenseCreditCardNotFound() {
        Expense expense = buildExpenseToUpdateNotFoundCreditCard();
        assertThrows(DataIntegrityViolationException.class, () -> expensePersistence.updateExpense(encodedUserPrincipal, expense));
    }

    @Test
    void testUpdateExpenseBankAccountNotFound() {
        Expense expense = buildExpenseToUpdateNotFoundBankAccount();
        assertThrows(DataIntegrityViolationException.class, () -> expensePersistence.updateExpense(encodedUserPrincipal, expense));
    }

    @Test
    @Transactional
    void testUpdateExpenseWithAllDependenciesSuccess() {
        Expense expense = buildExpenseToUpdateWithAllDependencies();
        ExpenseEntity updatedExpenseEntity = expensePersistence.updateExpense(encodedUserPrincipal, expense);
        assertNotNull(updatedExpenseEntity);
        assertEquals(expense.getAmount(), updatedExpenseEntity.getAmount());
        assertEquals(expense.getExpenseDate(), updatedExpenseEntity.getExpenseDate());
        assertEquals(expense.getDescription(), updatedExpenseEntity.getDescription());
        assertEquals(expense.getFormOfPayment().name(), updatedExpenseEntity.getFormOfPayment());
        assertNotNull(updatedExpenseEntity.getBankAccount());
        assertEquals(expense.getBankAccount().getUuid(), updatedExpenseEntity.getBankAccount().getUuid().toString());
        assertNotNull(updatedExpenseEntity.getCreditCard());
        assertEquals(expense.getCreditCard().getUuid(), updatedExpenseEntity.getCreditCard().getUuid().toString());
        assertNotNull(updatedExpenseEntity.getExpenseCategory());
        assertEquals(expense.getExpenseCategory().getUuid(), updatedExpenseEntity.getExpenseCategory().getUuid().toString());
    }

    @Test
    void testDeleteExpenseSuccess() {
        ExpenseEntity expenseEntity = expensePersistence.findExpenseByUserId(encodedUserPrincipal)
                .stream()
                .filter(expenseEntity1 -> expenseEntity1.getDescription().equals(TO_DELETE_EXPENSE_DESCRIPTION))
                .findFirst()
                .orElseThrow();
        expensePersistence.deleteExpense(encodedUserPrincipal, expenseEntity.getUuid().toString());
        assertTrue(expensePersistence.findExpenseByUserId(encodedUserPrincipal)
                .stream()
                .noneMatch(expenseEntity1 -> expenseEntity1.getDescription().equals(TO_DELETE_EXPENSE_DESCRIPTION)));
    }

    @Test
    void testDeleteExpenseExpenseNotFound() {
        assertThrows(ExpenseNotFoundException.class, () -> expensePersistence.deleteExpense(encodedUserPrincipal, RANDOM_UUID));
    }

    @Test
    void testGetExpensesGroupByDateChartDataSuccess() {
        List<DateChartData> dateChartDataList = expensePersistence.getExpensesGroupByDateChartData(encodedUserPrincipal);
        assertNotNull(dateChartDataList);
        assertFalse(dateChartDataList.isEmpty());
    }

    @Test
    void testGetExpensesGroupByDateChartDataEmpty() {
        assertTrue(expensePersistence.getExpensesGroupByDateChartData(UserPrincipal.builder().id(0).username("emptyUser").build()).isEmpty());
    }

    @Test
    void testGetExpensesGroupByCategoryChartDataSuccess() {
        List<ChartData> dateChartDataList = expensePersistence.getExpensesGroupByCategoryChartData(encodedUserPrincipal);
        assertNotNull(dateChartDataList);
        assertFalse(dateChartDataList.isEmpty());
    }

    @Test
    void testGetExpensesGroupByCategoryChartDataEmpty() {
        assertTrue(expensePersistence.getExpensesGroupByCategoryChartData(NOT_FOUND_USER_PRINCIPAL).isEmpty());
    }

    @Test
    void testGetExpensesGroupByFOPChartDataSuccess() {
        List<ChartData> dateChartDataList = expensePersistence.getExpensesGroupByFOPChartData(encodedUserPrincipal);
        assertNotNull(dateChartDataList);
        assertFalse(dateChartDataList.isEmpty());
    }

    @Test
    void testGetExpensesGroupByFOPChartDataEmpty() {
        assertTrue(expensePersistence.getExpensesGroupByFOPChartData(NOT_FOUND_USER_PRINCIPAL).isEmpty());
    }

    @Test
    void testGetExpensesGroupByCreditCardChartDataSuccess() {
        List<ChartData> dateChartDataList = expensePersistence.getExpensesGroupByCreditCardChartData(encodedUserPrincipal);
        assertNotNull(dateChartDataList);
        assertFalse(dateChartDataList.isEmpty());
    }

    @Test
    void testGetExpensesGroupByCreditCardChartDataEmpty() {
        assertTrue(expensePersistence.getExpensesGroupByCreditCardChartData(NOT_FOUND_USER_PRINCIPAL).isEmpty());
    }

    @Test
    void testGetExpensesGroupByBankAccountChartDataSuccess() {
        List<ChartData> dateChartDataList = expensePersistence.getExpensesGroupByBankAccountChartData(encodedUserPrincipal);
        assertNotNull(dateChartDataList);
        assertFalse(dateChartDataList.isEmpty());
    }

    @Test
    void testGetExpensesGroupByBankAccountChartDataEmpty() {
        assertTrue(expensePersistence.getExpensesGroupByBankAccountChartData(NOT_FOUND_USER_PRINCIPAL).isEmpty());
    }



    private Expense buildExpenseToUpdateWithAllDependencies() {
        return Expense.builder()
                .uuid(findExpenseWithDependenciesToUpdate().getUuid().toString())
                .amount(BigDecimal.ONE)
                .expenseDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .formOfPayment(FormOfPayment.CASH)
                .bankAccount(buildBankAccountEntityReference())
                .creditCard(buildCreditCardEntityReference())
                .expenseCategory(buildExpenseCategoryEntity())
                .build();
    }

    private Expense buildExpenseToUpdateNotFoundBankAccount() {
        return Expense.builder()
                .uuid(findExpenseToUpdate().getUuid().toString())
                .amount(BigDecimal.ONE)
                .expenseDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .formOfPayment(FormOfPayment.CASH)
                .bankAccount(BankAccount.builder().uuid(UUID.randomUUID().toString()).build())
                .build();
    }

    private Expense buildExpenseToUpdateNotFoundCreditCard() {
        return Expense.builder()
                .uuid(findExpenseToUpdate().getUuid().toString())
                .amount(BigDecimal.ONE)
                .expenseDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .formOfPayment(FormOfPayment.CASH)
                .creditCard(CreditCard.builder().uuid(UUID.randomUUID().toString()).build())
                .build();
    }

    private Expense buildExpenseToUpdateNotFoundExpenseCategory() {
        return Expense.builder()
                .uuid(findExpenseToUpdate().getUuid().toString())
                .amount(BigDecimal.TEN)
                .expenseDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .formOfPayment(FormOfPayment.CASH)
                .expenseCategory(ExpenseCategory.builder().uuid(UUID.randomUUID().toString()).build())
                .build();
    }

    private ExpenseEntity findExpenseToUpdate() {
        return expensePersistence.findExpenseByUserId(encodedUserPrincipal)
                .stream()
                .filter(expenseEntity -> expenseEntity.getCreditCard() != null && expenseEntity.getCreditCard().getCreditCardNumber().equals(TO_UPDATE_EXPENSE_CREDIT_CARD_NUMBER))
                .findFirst()
                .orElseThrow();
    }

    private ExpenseEntity findExpenseWithDependenciesToUpdate() {
        return expensePersistence.findExpenseByUserId(encodedUserPrincipal)
                .stream()
                .filter(expenseEntity -> expenseEntity.getAmount().compareTo(BigDecimal.TEN) == 0
                        && expenseEntity.getCreditCard() != null && expenseEntity.getCreditCard().getCreditCardNumber().equals(TO_UPDATE_EXPENSE_CREDIT_CARD_NUMBER))
                .findFirst()
                .orElseThrow();
    }

    private Expense buildExpenseToUpdate() {
        return Expense.builder()
                .uuid(findExpenseToUpdate().getUuid().toString())
                .amount(BigDecimal.ONE)
                .expenseDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .formOfPayment(FormOfPayment.CASH)
                .build();
    }


    private Expense buildExpenseWithDependencies() {
        return Expense.builder()
                .amount(BigDecimal.TEN)
                .expenseDate(Timestamp.valueOf(LocalDateTime.now()))
                .description("description")
                .formOfPayment(FormOfPayment.CASH)
                .bankAccount(buildBankAccountEntityReference())
                .creditCard(buildCreditCardEntityReference())
                .expenseCategory(buildExpenseCategoryEntity())
                .build();
    }

    private BankAccount buildBankAccountEntityReference() {
        return BankAccount.builder()
                .uuid(encodedUserEntity.getBankAccounts().get(0).getUuid().toString())
                .iban(encodedUserEntity.getBankAccounts().get(0).getIban())
                .build();
    }

    private CreditCard buildCreditCardEntityReference() {
        return CreditCard.builder()
                .uuid(encodedUserEntity.getCreditCards().get(0).getUuid().toString())
                .creditCardNumber(encodedUserEntity.getCreditCards().get(0).getCreditCardNumber())
                .build();
    }

    private ExpenseCategory buildExpenseCategoryEntity() {
        return ExpenseCategory.builder()
                .uuid(encodedUserEntity.getExpenseCategories().get(0).getUuid().toString())
                .name(encodedUserEntity.getExpenseCategories().get(0).getName())
                .build();
    }

    private Expense buildExpenseWithoutDependencies() {
        return Expense.builder()
                .amount(BigDecimal.TEN)
                .expenseDate(Timestamp.valueOf(LocalDateTime.now()))
                .description("description")
                .formOfPayment(FormOfPayment.CASH)
                .build();
    }
}
