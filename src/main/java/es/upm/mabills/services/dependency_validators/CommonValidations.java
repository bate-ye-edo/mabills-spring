package es.upm.mabills.services.dependency_validators;

import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.CreditCardNotFoundException;
import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.exceptions.InvalidRequestException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.UserEntity;

import java.util.Objects;
import java.util.Optional;
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

    public static void assertCreditCardBankAccountAreRelated(UserEntity user, CreditCard creditCard, BankAccount bankAccount) {
        if(Objects.nonNull(user) && Objects.nonNull(creditCard) && Objects.nonNull(bankAccount) && !isCreditCardRelatedToBankAccount(user, creditCard, bankAccount)){
            throw new InvalidRequestException("Credit card is not related to the bank account");
        }
    }

    private static boolean isCreditCardRelatedToBankAccount(UserEntity user, CreditCard creditCard, BankAccount bankAccount) {
        return Optional.of(user.getCreditCards()
                        .stream()
                        .filter(creditCardEntity -> creditCardEntity.getUuid().compareTo(UUID.fromString(creditCard.getUuid())) == 0)
                        .map(CreditCardEntity::getBankAccount)
                        .anyMatch(bankAccountEntity -> Optional.ofNullable(bankAccountEntity)
                                .map(BankAccountEntity::getUuid)
                                .map(uuid -> uuid.compareTo(UUID.fromString(bankAccount.getUuid())) == 0)
                                .orElse(false)
                        )
                )
                .orElse(false);
    }

    private CommonValidations() {}
}
