package es.upm.mabills.services.dependency_validators;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.CreditCardNotFoundException;
import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@UnitTestConfig
class CommonValidationsTest {
    private static final UUID RANDOM_UUID = UUID.randomUUID();
    private static final String OTHER_UUID = UUID.randomUUID().toString();
    private static final UserEntity USER_ENTITY = UserEntity.builder().build();
    private static final UserEntity USER_WITH_BANK_ACCOUNT = UserEntity.builder()
            .bankAccounts(List.of(BankAccountEntity.builder().uuid(RANDOM_UUID).build()))
            .build();
    private static final UserEntity USER_WITH_CREDIT_CARD = UserEntity.builder()
            .creditCards(List.of(CreditCardEntity.builder().uuid(RANDOM_UUID).build()))
            .build();
    private static final UserEntity USER_WITH_EXPENSE_CATEGORY = UserEntity.builder()
            .expenseCategories(List.of(ExpenseCategoryEntity.builder().uuid(RANDOM_UUID).build()))
            .build();

    @Test
    void testUserHasBankAccount() {
        assertDoesNotThrow(() -> CommonValidations.assertUserHasBankAccount(USER_WITH_BANK_ACCOUNT, BankAccount.builder().uuid(RANDOM_UUID.toString()).build()));
    }

    @Test
    void testUserHasCreditCard() {
        assertDoesNotThrow(() -> CommonValidations.assertUserHasCreditCard(USER_WITH_CREDIT_CARD, CreditCard.builder().uuid(RANDOM_UUID.toString()).build()));
    }

    @Test
    void testUserHasExpenseCategory() {
        assertDoesNotThrow(() -> CommonValidations.assertUserHasExpenseCategory(USER_WITH_EXPENSE_CATEGORY, ExpenseCategory.builder().uuid(RANDOM_UUID.toString()).build()));
    }

    @Test
    void testUserHasBankAccountNull() {
        assertDoesNotThrow(() -> CommonValidations.assertUserHasBankAccount(USER_ENTITY, null));
    }

    @Test
    void testUserHasCreditCardNull() {
        assertDoesNotThrow(() -> CommonValidations.assertUserHasCreditCard(USER_ENTITY, null));
    }

    @Test
    void testUserHasExpenseCategoryNull() {
        assertDoesNotThrow(() -> CommonValidations.assertUserHasExpenseCategory(USER_ENTITY, null));
    }

    @Test
    void testUserHasBankAccountUserWithoutBankAccount() {
        BankAccount bankAccount = BankAccount.builder().uuid(OTHER_UUID).build();
        assertDoesNotThrow(() -> CommonValidations.assertUserHasBankAccount(USER_ENTITY, bankAccount));
    }

    @Test
    void testUserHasCreditCardNullUserWithoutCreditCard() {
        CreditCard creditCard = CreditCard.builder().uuid(OTHER_UUID).build();
        assertDoesNotThrow(() -> CommonValidations.assertUserHasCreditCard(USER_ENTITY, creditCard));
    }

    @Test
    void testUserHasExpenseCategoryNullUserWithoutExpenseCategories() {
        ExpenseCategory expenseCategory = ExpenseCategory.builder().uuid(OTHER_UUID).build();
        assertDoesNotThrow(() -> CommonValidations.assertUserHasExpenseCategory(USER_ENTITY, expenseCategory));
    }


    @Test
    void testUserHasBankAccountNotFound() {
        BankAccount bankAccount = BankAccount.builder().uuid(OTHER_UUID).build();
        assertThrows(BankAccountNotFoundException.class, () -> CommonValidations.assertUserHasBankAccount(USER_WITH_BANK_ACCOUNT, bankAccount));
    }

    @Test
    void testUserHasCreditCardNotFound() {
        CreditCard creditCard = CreditCard.builder().uuid(OTHER_UUID).build();
        assertThrows(CreditCardNotFoundException.class, () -> CommonValidations.assertUserHasCreditCard(USER_WITH_CREDIT_CARD, creditCard));
    }

    @Test
    void testUserHasExpenseCategoryNotFound() {
        ExpenseCategory expenseCategory = ExpenseCategory.builder().uuid(OTHER_UUID).build();
        assertThrows(ExpenseCategoryNotFoundException.class, () -> CommonValidations.assertUserHasExpenseCategory(USER_WITH_EXPENSE_CATEGORY, expenseCategory));
    }
}
