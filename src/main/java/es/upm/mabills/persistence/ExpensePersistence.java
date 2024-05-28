package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.model.FormOfPayment;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.CreditCardRepository;
import es.upm.mabills.persistence.repositories.ExpenseCategoryRepository;
import es.upm.mabills.persistence.repositories.ExpenseRepository;
import es.upm.mabills.persistence.repositories.RepositorySort;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ExpensePersistence {
    private final ExpenseRepository expenseRepository;
    private final EntityReferenceFactory entityReferenceFactory;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final CreditCardRepository creditCardRepository;

    @Autowired
    public ExpensePersistence(ExpenseRepository expenseRepository, EntityReferenceFactory entityReferenceFactory,
                              ExpenseCategoryRepository expenseCategoryRepository, CreditCardRepository creditCardRepository) {
        this.expenseRepository = expenseRepository;
        this.entityReferenceFactory = entityReferenceFactory;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.creditCardRepository = creditCardRepository;
    }

    public List<ExpenseEntity> findExpenseByUserId(UserPrincipal userPrincipal) {
        return expenseRepository.findByUserId(userPrincipal.getId(), RepositorySort.BY_CREATION_DATE.value());
    }

    @Transactional
    public ExpenseEntity createExpense(UserPrincipal userPrincipal, Expense expense) {
        return Try.of(() -> buildExpense(userPrincipal, expense))
                .map(expenseRepository::save)
                .get();
    }

    @Transactional
    public ExpenseEntity updateExpense(UserPrincipal userPrincipal, Expense expense) {
        return Try.of(() -> expenseRepository.findByUserIdAndUuid(userPrincipal.getId(), UUID.fromString(expense.getUuid())))
                .map(expenseEntity -> {
                    expenseEntity.setAmount(expense.getAmount());
                    expenseEntity.setExpenseDate(expense.getExpenseDate());
                    expenseEntity.setDescription(expense.getDescription());
                    expenseEntity.setFormOfPayment(buildFormOfPaymentName(expense.getFormOfPayment()));
                    expenseEntity.setBankAccount(buildBankAccountEntityReference(expense.getBankAccount(), expense.getCreditCard(), userPrincipal));
                    expenseEntity.setCreditCard(buildCreditCardEntityReference(expense.getCreditCard()));
                    expenseEntity.setExpenseCategory(buildToUpdateExpenseCategoryEntity(userPrincipal, expense.getExpenseCategory()));
                    return expenseRepository.save(expenseEntity);
                })
                .get();
    }

    private ExpenseEntity buildExpense(UserPrincipal userPrincipal, Expense expense) {
        return ExpenseEntity.builder()
                .amount(expense.getAmount())
                .user(entityReferenceFactory.buildReference(UserEntity.class, userPrincipal.getId()))
                .expenseDate(expense.getExpenseDate())
                .description(expense.getDescription())
                .formOfPayment(buildFormOfPaymentName(expense.getFormOfPayment()))
                .bankAccount(buildBankAccountEntityReference(expense.getBankAccount(), expense.getCreditCard(), userPrincipal))
                .creditCard(buildCreditCardEntityReference(expense.getCreditCard()))
                .expenseCategory(buildExpenseCategoryEntity(userPrincipal, expense.getExpenseCategory()))
                .build();
    }

    private String buildFormOfPaymentName(FormOfPayment formOfPayment) {
        return Objects.isNull(formOfPayment) ? null : formOfPayment.name();
    }

    private ExpenseCategoryEntity buildExpenseCategoryEntity(UserPrincipal userPrincipal, ExpenseCategory expenseCategory) {
        return Optional.ofNullable(expenseCategory)
                .map(ExpenseCategory::getUuid)
                .map(UUID::fromString)
                .map(uuid -> expenseCategoryRepository.findByUser_UsernameAndUuid(userPrincipal.getUsername(), uuid))
                .orElse(null);
    }

    private CreditCardEntity buildCreditCardEntityReference(CreditCard creditCard) {
        return Optional.ofNullable(creditCard)
                .map(CreditCard::getUuid)
                .map(UUID::fromString)
                .map(uuid -> entityReferenceFactory.buildReference(CreditCardEntity.class, uuid))
                .orElse(null);
    }

    private BankAccountEntity buildBankAccountEntityReference(BankAccount bankAccount, CreditCard creditCard, UserPrincipal userPrincipal) {
        return Optional.ofNullable(bankAccount)
                .map(BankAccount::getUuid)
                .map(UUID::fromString)
                .map(uuid -> entityReferenceFactory.buildReference(BankAccountEntity.class, uuid))
                .orElse(buildBankAccountEntityReferenceFromCreditCard(userPrincipal, creditCard));
    }

    private BankAccountEntity buildBankAccountEntityReferenceFromCreditCard(UserPrincipal userPrincipal, CreditCard creditCard) {
        return Optional.ofNullable(creditCard)
                .map(creditcard -> creditCardRepository.findByUserIdAndUuid(userPrincipal.getId(), UUID.fromString(creditCard.getUuid())))
                .map(CreditCardEntity::getBankAccount)
                .map(BankAccountEntity::getUuid)
                .map(uuid -> entityReferenceFactory.buildReference(BankAccountEntity.class, uuid))
                .orElse(null);
    }

    private ExpenseCategoryEntity buildToUpdateExpenseCategoryEntity(UserPrincipal userPrincipal, ExpenseCategory expenseCategory) {
        ExpenseCategoryEntity expenseCategoryEntity = buildExpenseCategoryEntity(userPrincipal, expenseCategory);
        if (Objects.nonNull(expenseCategory) && Objects.isNull(expenseCategoryEntity)) {
            throw new ExpenseCategoryNotFoundException();
        }
        return expenseCategoryEntity;
    }
}
