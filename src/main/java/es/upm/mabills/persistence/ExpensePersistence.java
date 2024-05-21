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
import es.upm.mabills.persistence.repositories.ExpenseCategoryRepository;
import es.upm.mabills.persistence.repositories.ExpenseRepository;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ExpensePersistence {
    private final ExpenseRepository expenseRepository;
    private final EntityReferenceFactory entityReferenceFactory;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Autowired
    public ExpensePersistence(ExpenseRepository expenseRepository, EntityReferenceFactory entityReferenceFactory,
                              ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseRepository = expenseRepository;
        this.entityReferenceFactory = entityReferenceFactory;
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    public List<ExpenseEntity> findExpenseByUserId(UserPrincipal userPrincipal) {
        return expenseRepository.findByUserId(userPrincipal.getId());
    }

    public ExpenseEntity createExpense(UserPrincipal userPrincipal, Expense expense) {
        return Try.of(() -> buildExpense(userPrincipal, expense))
                .map(expenseRepository::save)
                .get();
    }

    private ExpenseEntity buildExpense(UserPrincipal userPrincipal, Expense expense) {
        return ExpenseEntity.builder()
                .amount(expense.getAmount())
                .user(entityReferenceFactory.buildReference(UserEntity.class, userPrincipal.getId()))
                .expenseDate(expense.getExpenseDate())
                .description(expense.getDescription())
                .formOfPayment(expense.getFormOfPayment().name())
                .bankAccount(buildBankAccountEntity(expense.getBankAccount()))
                .creditCard(buildCreditCardEntity(expense.getCreditCard()))
                .expenseCategory(buildExpenseCategoryEntity(userPrincipal, expense.getExpenseCategory()))
                .build();
    }

    private ExpenseCategoryEntity buildExpenseCategoryEntity(UserPrincipal userPrincipal, ExpenseCategory expenseCategory) {
        return Optional.ofNullable(expenseCategory)
                .map(ExpenseCategory::getUuid)
                .map(UUID::fromString)
                .map(uuid -> expenseCategoryRepository.findByUser_UsernameAndUuid(userPrincipal.getUsername(), uuid))
                .orElse(null);
    }

    private CreditCardEntity buildCreditCardEntity(CreditCard creditCard) {
        return Optional.ofNullable(creditCard)
                .map(CreditCard::getUuid)
                .map(UUID::fromString)
                .map(uuid -> entityReferenceFactory.buildReference(CreditCardEntity.class, uuid))
                .orElse(null);
    }

    private BankAccountEntity buildBankAccountEntity(BankAccount bankAccount) {
        return Optional.ofNullable(bankAccount)
                .map(BankAccount::getUuid)
                .map(UUID::fromString)
                .map(uuid -> entityReferenceFactory.buildReference(BankAccountEntity.class, uuid))
                .orElse(null);
    }
}
