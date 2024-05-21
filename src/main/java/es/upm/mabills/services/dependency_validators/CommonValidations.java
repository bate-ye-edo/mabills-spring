package es.upm.mabills.services.dependency_validators;

import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.CreditCardNotFoundException;
import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.persistence.entities.UserEntity;

import java.util.Objects;
import java.util.UUID;

public final class CommonValidations {
    public static void assertUserHasBankAccount(UserEntity user, BankAccount bankAccount) {
        if(Objects.nonNull(bankAccount) && Objects.nonNull(user.getBankAccounts()) && !isBankAccountValid(user, bankAccount)) {
            throw new BankAccountNotFoundException(bankAccount.getIban());
        }
    }

    private static boolean isBankAccountValid(UserEntity user, BankAccount bankAccount) {
        return user.getBankAccounts()
                .stream()
                .anyMatch(bankAccountEntity -> bankAccountEntity.getUuid()
                        .compareTo(UUID.fromString(bankAccount.getUuid())) == 0);
    }

    public static void assertUserHasCreditCard(UserEntity user, CreditCard creditCard) {
        if(Objects.nonNull(creditCard) && Objects.nonNull(user.getCreditCards()) && !isCreditCardValid(user, creditCard)) {
            throw new CreditCardNotFoundException(creditCard.getCreditCardNumber());
        }
    }

    private static boolean isCreditCardValid(UserEntity user, CreditCard creditCard) {
        return user.getCreditCards()
                .stream()
                .anyMatch(creditCardEntity -> creditCardEntity.getUuid()
                        .compareTo(UUID.fromString(creditCard.getUuid())) == 0);
    }

    public static void assertUserHasExpenseCategory(UserEntity user, ExpenseCategory expenseCategory) {
        if(Objects.nonNull(expenseCategory) && Objects.nonNull(user.getExpenseCategories()) && !isExpenseCategoryValid(user, expenseCategory)) {
            throw new ExpenseCategoryNotFoundException(UUID.fromString(expenseCategory.getUuid()));
        }
    }

    private static boolean isExpenseCategoryValid(UserEntity user, ExpenseCategory expenseCategory) {
        return user.getExpenseCategories()
                .stream()
                .anyMatch(expenseCategoryEntity -> expenseCategoryEntity.getUuid()
                        .compareTo(UUID.fromString(expenseCategory.getUuid())) == 0);
    }
}
