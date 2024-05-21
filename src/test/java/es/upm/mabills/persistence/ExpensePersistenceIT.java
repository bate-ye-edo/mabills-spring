package es.upm.mabills.persistence;


import es.upm.mabills.TestConfig;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.model.FormOfPayment;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class ExpensePersistenceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final UserPrincipal NOT_FOUND_USER_PRINCIPAL = UserPrincipal.builder()
            .id(0)
            .username("notFoundUser")
            .build();

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
    @Transactional
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

    private Expense buildExpenseWithDependencies() {
        return Expense.builder()
                .amount(BigDecimal.TEN)
                .expenseDate(Timestamp.valueOf(LocalDateTime.now()))
                .description("description")
                .formOfPayment(FormOfPayment.CASH)
                .bankAccount(buildBankAccountEntity())
                .creditCard(buildCreditCardEntity())
                .expenseCategory(buildExpenseCategoryEntity())
                .build();
    }

    private BankAccount buildBankAccountEntity() {
        return BankAccount.builder()
                .uuid(encodedUserEntity.getBankAccounts().get(0).getUuid().toString())
                .iban(encodedUserEntity.getBankAccounts().get(0).getIban())
                .build();
    }

    private CreditCard buildCreditCardEntity() {
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
