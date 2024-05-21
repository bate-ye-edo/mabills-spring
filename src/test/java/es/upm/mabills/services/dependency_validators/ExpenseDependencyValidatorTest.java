package es.upm.mabills.services.dependency_validators;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.CreditCardNotFoundException;
import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.UserPersistence;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@UnitTestConfig
class ExpenseDependencyValidatorTest {
    private static final UUID RANDOM_UUID = UUID.randomUUID();
    private static final String RANDOM_UUID_STRING = RANDOM_UUID.toString();
    private static final String OTHER_RANDOM_UUID_STRING = UUID.randomUUID().toString();

    @MockBean
    private UserPersistence userPersistence;

    @Autowired
    private ExpenseDependencyValidator expenseDependencyValidator;

    private static final UserPrincipal USER_PRINCIPAL = UserPrincipal.builder().username("").build();


    @Test
    void testAssertDependenciesUserWithoutDependenciesSuccess() {
        when(userPersistence.findUserByUsername(anyString())).thenReturn(UserEntity.builder().build());
        assertDoesNotThrow(() -> expenseDependencyValidator.assertDependencies(USER_PRINCIPAL,
                Expense.builder().build()));
    }

    @Test
    void testAssertDependenciesUserWithDependenciesSuccess() {
        UserEntity user = buildUserEntityWithDependencies();
        Expense expense = buildExpenseWithDependencies();
        when(userPersistence.findUserByUsername(anyString())).thenReturn(user);
        assertDoesNotThrow(() -> expenseDependencyValidator.assertDependencies(USER_PRINCIPAL, expense));
    }

    @Test
    void testAssertDependenciesBankAccountNotFoundException() {
        UserEntity user = buildUserEntityWithDependencies();
        Expense expense = Expense.builder().bankAccount(BankAccount.builder().uuid(OTHER_RANDOM_UUID_STRING).build()).build();
        when(userPersistence.findUserByUsername(anyString())).thenReturn(user);
        assertThrows(BankAccountNotFoundException.class, () -> expenseDependencyValidator.assertDependencies(USER_PRINCIPAL, expense));
    }

    @Test
    void testAssertDependenciesCreditCardNotFoundException() {
        UserEntity user = buildUserEntityWithDependencies();
        Expense expense = Expense.builder().creditCard(CreditCard.builder().uuid(OTHER_RANDOM_UUID_STRING).build()).build();
        when(userPersistence.findUserByUsername(anyString())).thenReturn(user);
        assertThrows(CreditCardNotFoundException.class, () -> expenseDependencyValidator.assertDependencies(USER_PRINCIPAL, expense));
    }

    @Test
    void testAssertDependenciesExpenseCategoryNotFoundException() {
        UserEntity user = buildUserEntityWithDependencies();
        Expense expense = Expense.builder().expenseCategory(ExpenseCategory.builder().uuid(OTHER_RANDOM_UUID_STRING).build()).build();
        when(userPersistence.findUserByUsername(anyString())).thenReturn(user);
        assertThrows(ExpenseCategoryNotFoundException.class, () -> expenseDependencyValidator.assertDependencies(USER_PRINCIPAL, expense));
    }

    private UserEntity buildUserEntityWithDependencies() {
        return UserEntity.builder()
                .bankAccounts(List.of(BankAccountEntity.builder().uuid(RANDOM_UUID).build()))
                .creditCards(List.of(CreditCardEntity.builder().uuid(RANDOM_UUID).build()))
                .expenseCategories(List.of(ExpenseCategoryEntity.builder().uuid(RANDOM_UUID).build()))
                .build();
    }

    private Expense buildExpenseWithDependencies() {
        return Expense.builder()
                .bankAccount(BankAccount.builder().uuid(RANDOM_UUID_STRING).build())
                .creditCard(CreditCard.builder().uuid(RANDOM_UUID_STRING).build())
                .expenseCategory(ExpenseCategory.builder().uuid(RANDOM_UUID_STRING).build())
                .build();
    }
}
