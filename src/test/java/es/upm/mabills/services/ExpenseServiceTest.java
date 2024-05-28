package es.upm.mabills.services;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.CreditCardNotFoundException;
import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.FormOfPayment;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.ExpensePersistence;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.services.dependency_validators.DependencyValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@UnitTestConfig
class ExpenseServiceTest {
    @Autowired
    private ExpenseService expenseService;

    @MockBean
    private ExpensePersistence expensePersistence;

    @MockBean(name = "expenseDependencyValidator")
    private DependencyValidator dependencyValidator;

    @Test
    void testFindExpenseByUserId() {
        when(expensePersistence.findExpenseByUserId(any())).thenReturn(List.of(ExpenseEntity.builder().build()));
        assertFalse(expensePersistence.findExpenseByUserId(UserPrincipal.builder().build()).isEmpty());
    }

    @Test
    void testFindExpenseByUserIdNotFound() {
        when(expensePersistence.findExpenseByUserId(any())).thenReturn(List.of());
        assertTrue(expensePersistence.findExpenseByUserId(UserPrincipal.builder().build()).isEmpty());
    }

    @Test
    void testCreateExpenseSuccess() {
        ExpenseEntity expenseEntity = buildExpenseEntity();
        when(expensePersistence.createExpense(any(), any())).thenReturn(expenseEntity);
        Expense createdExpense = expenseService.createExpense(UserPrincipal.builder().build(), Expense.builder().build());
        assertNotNull(createdExpense);
        assertEquals(expenseEntity.getAmount(), createdExpense.getAmount());
        assertEquals(expenseEntity.getDescription(), createdExpense.getDescription());
        assertEquals(expenseEntity.getFormOfPayment(), createdExpense.getFormOfPayment().name());
        assertEquals(expenseEntity.getExpenseDate(), createdExpense.getExpenseDate());
    }

    @Test
    void testCreateExpenseFailureByBankAccount() {
        doThrow(BankAccountNotFoundException.class).when(dependencyValidator).assertDependencies(any(), any());
        Expense expense = Expense.builder().build();
        UserPrincipal userPrincipal = UserPrincipal.builder().build();
        assertThrows(BankAccountNotFoundException.class, () -> expenseService.createExpense(userPrincipal, expense));
    }

    @Test
    void testCreateExpenseFailureByCreditCard() {
        doThrow(CreditCardNotFoundException.class).when(dependencyValidator).assertDependencies(any(), any());
        Expense expense = Expense.builder().build();
        UserPrincipal userPrincipal = UserPrincipal.builder().build();
        assertThrows(CreditCardNotFoundException.class, () -> expenseService.createExpense(userPrincipal, expense));
    }

    @Test
    void testCreateExpenseFailureByExpenseCategory() {
        doThrow(ExpenseCategoryNotFoundException.class).when(dependencyValidator).assertDependencies(any(), any());
        Expense expense = Expense.builder().build();
        UserPrincipal userPrincipal = UserPrincipal.builder().build();
        assertThrows(ExpenseCategoryNotFoundException.class, () -> expenseService.createExpense(userPrincipal, expense));
    }

    @Test
    void testUpdateExpenseSuccess() {
        ExpenseEntity expenseEntity = buildExpenseEntity();
        when(expensePersistence.updateExpense(any(), any())).thenReturn(expenseEntity);
        Expense updatedExpense = expenseService.updateExpense(UserPrincipal.builder().build(), Expense.builder().build());
        assertNotNull(updatedExpense);
        assertEquals(expenseEntity.getAmount(), updatedExpense.getAmount());
        assertEquals(expenseEntity.getDescription(), updatedExpense.getDescription());
        assertEquals(expenseEntity.getFormOfPayment(), updatedExpense.getFormOfPayment().name());
        assertEquals(expenseEntity.getExpenseDate(), updatedExpense.getExpenseDate());
    }

    @Test
    void testUpdateExpenseFailureByBankAccountNotFoundByDependencyValidator() {
        doThrow(BankAccountNotFoundException.class).when(dependencyValidator).assertDependencies(any(), any());
        Expense expense = Expense.builder().build();
        UserPrincipal userPrincipal = UserPrincipal.builder().build();
        assertThrows(BankAccountNotFoundException.class, () -> expenseService.updateExpense(userPrincipal, expense));
    }

    @Test
    void testUpdateExpenseFailureByCreditCardNotFoundByDependencyValidator() {
        doThrow(CreditCardNotFoundException.class).when(dependencyValidator).assertDependencies(any(), any());
        Expense expense = Expense.builder().build();
        UserPrincipal userPrincipal = UserPrincipal.builder().build();
        assertThrows(CreditCardNotFoundException.class, () -> expenseService.updateExpense(userPrincipal, expense));
    }

    @Test
    void testUpdateExpenseFailureByExpenseCategoryNotFoundByDependencyValidator() {
        doThrow(ExpenseCategoryNotFoundException.class).when(dependencyValidator).assertDependencies(any(), any());
        Expense expense = Expense.builder().build();
        UserPrincipal userPrincipal = UserPrincipal.builder().build();
        assertThrows(ExpenseCategoryNotFoundException.class, () -> expenseService.updateExpense(userPrincipal, expense));
    }


    private ExpenseEntity buildExpenseEntity() {
        return ExpenseEntity.builder()
                .expenseDate(Timestamp.valueOf(LocalDateTime.now().minusDays(5)))
                .amount(BigDecimal.TEN)
                .description("description")
                .formOfPayment(FormOfPayment.CASH.name())
                .build();
    }

}
