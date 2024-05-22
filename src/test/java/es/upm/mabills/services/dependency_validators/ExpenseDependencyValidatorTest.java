package es.upm.mabills.services.dependency_validators;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.CreditCardNotFoundException;
import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.exceptions.InvalidRequestException;
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
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        when(userPersistence.findUserByUsername(anyString())).thenReturn(buildUserEntityWithDependencies());
    }

    @Test
    void testAssertDependenciesUserWithoutDependenciesSuccess() {
        when(userPersistence.findUserByUsername(anyString())).thenReturn(UserEntity.builder().build());
        assertDoesNotThrow(() -> expenseDependencyValidator.assertDependencies(USER_PRINCIPAL,
                Expense.builder().build()));
    }

    @Test
    void testAssertDependenciesUserWithDependenciesSuccess() {
        Expense expense = buildExpenseWithDependencies();
        assertDoesNotThrow(() -> expenseDependencyValidator.assertDependencies(USER_PRINCIPAL, expense));
    }

    @Test
    void testAssertDependenciesBankAccountNotFoundException() {
        Expense expense = Expense.builder().bankAccount(BankAccount.builder().uuid(OTHER_RANDOM_UUID_STRING).build()).build();
        assertThrows(BankAccountNotFoundException.class, () -> expenseDependencyValidator.assertDependencies(USER_PRINCIPAL, expense));
    }

    @Test
    void testAssertDependenciesCreditCardNotFoundException() {
        Expense expense = Expense.builder().creditCard(CreditCard.builder().uuid(OTHER_RANDOM_UUID_STRING).build()).build();
        assertThrows(CreditCardNotFoundException.class, () -> expenseDependencyValidator.assertDependencies(USER_PRINCIPAL, expense));
    }

    @Test
    void testAssertDependenciesExpenseCategoryNotFoundException() {
        Expense expense = Expense.builder().expenseCategory(ExpenseCategory.builder().uuid(OTHER_RANDOM_UUID_STRING).build()).build();
        assertThrows(ExpenseCategoryNotFoundException.class, () -> expenseDependencyValidator.assertDependencies(USER_PRINCIPAL, expense));
    }

    @Test
    void testAssertDependenciesBankAccountNotRelatedToCreditCard() {
        Expense expense = buildExpenseWithCreditCardNotRelatedToBankAccount();
        when(userPersistence.findUserByUsername(anyString())).thenReturn(buildUserEntityWithTwoBankAccountsAndOneCreditCardRelated());
        assertThrows(InvalidRequestException.class, () -> expenseDependencyValidator.assertDependencies(USER_PRINCIPAL, expense));
    }

    private Expense buildExpenseWithCreditCardNotRelatedToBankAccount() {
        return Expense.builder()
                .creditCard(
                        CreditCard.builder()
                                .uuid(RANDOM_UUID_STRING)
                                .build())
                .bankAccount(
                        BankAccount.builder()
                                .uuid(OTHER_RANDOM_UUID_STRING)
                                .build()
                )
                .build();
    }

    private UserEntity buildUserEntityWithDependencies() {
        BankAccountEntity bankAccountEntity = BankAccountEntity.builder().uuid(RANDOM_UUID).build();
        CreditCardEntity creditCardEntity = CreditCardEntity.builder().uuid(RANDOM_UUID).bankAccount(bankAccountEntity).build();
        ExpenseCategoryEntity expenseCategoryEntity =ExpenseCategoryEntity.builder().uuid(RANDOM_UUID).build();
        return UserEntity.builder()
                .bankAccounts(List.of(bankAccountEntity))
                .creditCards(List.of(creditCardEntity))
                .expenseCategories(List.of(expenseCategoryEntity))
                .build();
    }

    private UserEntity buildUserEntityWithTwoBankAccountsAndOneCreditCardRelated() {
        BankAccountEntity bankAccountEntity = BankAccountEntity.builder().uuid(RANDOM_UUID).build();
        CreditCardEntity creditCardEntity = CreditCardEntity.builder().uuid(RANDOM_UUID).bankAccount(bankAccountEntity).build();
        BankAccountEntity anotherBankAccount = BankAccountEntity.builder().uuid(UUID.fromString(OTHER_RANDOM_UUID_STRING)).build();
        return UserEntity.builder()
                .bankAccounts(List.of(bankAccountEntity, anotherBankAccount))
                .creditCards(List.of(creditCardEntity))
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
