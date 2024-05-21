package es.upm.mabills.persistence;

import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.ExpenseRepository;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExpensePersistence {
    private final ExpenseRepository expenseRepository;
    private final EntityReferenceFactory entityReferenceFactory;
    private final UserPersistence userPersistence;
    @Autowired
    public ExpensePersistence(ExpenseRepository expenseRepository, EntityReferenceFactory entityReferenceFactory,
                              UserPersistence userPersistence) {
        this.expenseRepository = expenseRepository;
        this.entityReferenceFactory = entityReferenceFactory;
        this.userPersistence = userPersistence;
    }

    public List<ExpenseEntity> findExpenseByUserId(UserPrincipal userPrincipal) {
        return expenseRepository.findByUserId(userPrincipal.getId());
    }

    public ExpenseEntity createExpense(UserPrincipal userPrincipal, Expense expense) {
        return Try.of(() -> userPersistence.findUserByUsername(userPrincipal.getUsername()))
                .map(user -> buildExpense(user, expense))
                .map(expenseRepository::save)
                .get();
    }

    private ExpenseEntity buildExpense(UserEntity user, Expense expense) {
        return ExpenseEntity.builder()
                .amount(expense.getAmount())
                .user(entityReferenceFactory.buildReference(UserEntity.class, user.getId()))
                .expenseDate(expense.getExpenseDate())
                .description(expense.getDescription())
                .formOfPayment(expense.getFormOfPayment().name())
                .bankAccount(buildBankAccount(user, expense.getBankAccount()))
                .creditCard(buildCreditCard(user, expense.getCreditCard()))
                .expenseCategory(buildExpenseCategory(user, expense.getExpenseCategory()))
                .build();
    }

    private ExpenseCategoryEntity buildExpenseCategory(UserEntity user, ExpenseCategory expenseCategory) {
        userPersistence.assertUserHasExpenseCategory(user, expenseCategory);
        return Optional.ofNullable(expenseCategory)
                .map(ExpenseCategory::getUuid)
                .map(uuid -> entityReferenceFactory.buildReference(ExpenseCategoryEntity.class, uuid))
                .orElse(null);
    }

    private CreditCardEntity buildCreditCard(UserEntity user, CreditCard creditCard) {
        userPersistence.assertUserHasCreditCard(user, creditCard);
        return Optional.ofNullable(creditCard)
                .map(CreditCard::getUuid)
                .map(uuid -> entityReferenceFactory.buildReference(CreditCardEntity.class, uuid))
                .orElse(null);
    }

    private BankAccountEntity buildBankAccount(UserEntity user, BankAccount bankAccount) {
        userPersistence.assertUserHasBankAccount(user, bankAccount);
        return Optional.ofNullable(bankAccount)
                .map(BankAccount::getUuid)
                .map(uuid -> entityReferenceFactory.buildReference(BankAccountEntity.class, uuid))
                .orElse(null);
    }
}
