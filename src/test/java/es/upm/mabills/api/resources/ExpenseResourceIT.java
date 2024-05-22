package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
import es.upm.mabills.persistence.repositories.CreditCardRepository;
import es.upm.mabills.persistence.repositories.ExpenseCategoryRepository;
import es.upm.mabills.persistence.repositories.RepositorySort;
import es.upm.mabills.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ApiTestConfig
class ExpenseResourceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String ONLY_USER = "onlyUser";
    private static final String PASSWORD = "password";
    private static final String BANK_ACCOUNT_IBAN_WITH_CREDIT_CARD = "ES004120003120034012";
    private static final String CREDIT_CARD_NUMBER_RELATED_TO_BANK_ACCOUNT = "004120012352345632";
    private static final Timestamp TODAY = new Timestamp(System.currentTimeMillis());
    private static final UUID RANDOM_UUID = UUID.randomUUID();


    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    private UserEntity encodedUserEntity;


    @BeforeEach
    void setUp() {
        encodedUserEntity = userRepository.findByUsername(ENCODED_PASSWORD_USER);
    }


    @Test
    void testGetUserExpensesSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .get()
                .uri(ExpenseResource.EXPENSES)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Expense.class)
                .value(expensesList -> assertFalse(expensesList.isEmpty()));
    }

    @Test
    void testGetUserExpensesEmpty() {
        restClientTestService
                .login(webTestClient, buildOnlyUserLoginDto())
                .get()
                .uri(ExpenseResource.EXPENSES)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Expense.class)
                .value(expensesList -> assertTrue(expensesList.isEmpty()));
    }

    @Test
    void testGetUserExpensesUnauthorized() {
        webTestClient
                .get()
                .uri(ExpenseResource.EXPENSES)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void testCreateExpenseBadRequest() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(Expense.builder().build())
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void testCreateExpenseSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(buildExpense())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Expense.class)
                .value(expense -> {
                    assertNotNull(expense.getUuid());
                    assertNotNull(expense.getCreationDate());
                });
    }

    @Test
    void testCreateExpenseWithDependenciesThrowsCreditCardNotRelatedToBankAccount() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(buildExpenseWithDependenciesCreditCardNotRelatedToBankAccount())
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void testCreateExpenseWithDependenciesSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(buildExpenseWithDependencies())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Expense.class)
                .value(expense -> {
                    assertNotNull(expense);
                    assertNotNull(expense.getExpenseCategory());
                    assertNotNull(expense.getAmount());
                    assertNotNull(expense.getCreditCard());
                    assertNotNull(expense.getBankAccount());
                });
    }

    @Test
    void testCreateExpenseUnauthorized() {
        webTestClient
                .post()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(buildExpense())
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void testCreateExpenseBankAccountNotFound() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(buildExpenseBankAccountRandom())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testCreateExpenseCreditCardNotFound() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(buildExpenseCreditCardRandom())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testCreateExpenseExpenseCategoryNotFound() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(buildExpenseExpenseCategoryRandom())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    private Expense buildExpenseExpenseCategoryRandom() {
        return getDefaultExpenseBuilder()
                .expenseCategory(buildRandomExpenseCategory())
                .build();
    }

    private ExpenseCategory buildRandomExpenseCategory() {
        return ExpenseCategory.builder()
                .uuid(RANDOM_UUID.toString())
                .build();
    }

    private Expense buildExpenseCreditCardRandom() {
        return getDefaultExpenseBuilder()
                .creditCard(buildRandomCreditCard())
                .build();
    }

    private CreditCard buildRandomCreditCard() {
        return CreditCard.builder()
                .uuid(RANDOM_UUID.toString())
                .build();
    }

    private Expense buildExpenseBankAccountRandom() {
        return getDefaultExpenseBuilder()
                .bankAccount(buildRandomBankAccount())
                .build();
    }

    private BankAccount buildRandomBankAccount() {
        return BankAccount.builder()
                .uuid(RANDOM_UUID.toString())
                .build();
    }

    private Expense buildExpenseWithDependencies() {
        return getDefaultExpenseBuilder()
                .bankAccount(buildBankAccountWithCreditCard())
                .creditCard(buildCreditCardWithBankAccountRelated())
                .expenseCategory(buildExpenseCategory())
                .build();
    }

    private BankAccount buildBankAccountWithCreditCard() {
        return BankAccount.builder()
                .uuid(bankAccountRepository.findByUserId(encodedUserEntity.getId())
                        .stream()
                        .filter(bankAccountEntity -> bankAccountEntity.getIban().equals(BANK_ACCOUNT_IBAN_WITH_CREDIT_CARD))
                        .findFirst()
                        .orElse(BankAccountEntity.builder().uuid(RANDOM_UUID).build())
                        .getUuid()
                        .toString())
                .build();
    }

    private CreditCard buildCreditCardWithBankAccountRelated() {
        return CreditCard.builder()
                .uuid(creditCardRepository.findByUserId(encodedUserEntity.getId(), RepositorySort.BY_CREATION_DATE.value())
                        .stream()
                        .filter(creditCardEntity -> creditCardEntity.getCreditCardNumber().equals(CREDIT_CARD_NUMBER_RELATED_TO_BANK_ACCOUNT))
                        .findFirst()
                        .orElse(CreditCardEntity.builder().uuid(RANDOM_UUID).build())
                        .getUuid()
                        .toString()
                )
                .build();
    }

    private Expense buildExpenseWithDependenciesCreditCardNotRelatedToBankAccount() {
        return getDefaultExpenseBuilder()
                .bankAccount(buildBankAccount())
                .creditCard(buildCreditCard())
                .expenseCategory(buildExpenseCategory())
                .build();
    }

    private BankAccount buildBankAccount() {
        return BankAccount.builder()
                .uuid(bankAccountRepository.findByUserId(encodedUserEntity.getId())
                        .get(0)
                        .getUuid()
                        .toString())
                .build();
    }

    private CreditCard buildCreditCard() {
        return CreditCard.builder()
                .uuid(creditCardRepository.findByUserId(encodedUserEntity.getId(), RepositorySort.BY_CREATION_DATE.value())
                        .stream()
                        .filter(creditCardEntity -> Objects.nonNull(creditCardEntity.getBankAccount()))
                        .toList()
                        .get(0)
                        .getUuid().toString()
                )
                .build();
    }

    private ExpenseCategory buildExpenseCategory() {
        return ExpenseCategory.builder()
                .uuid(expenseCategoryRepository.findByUser_Username(encodedUserEntity.getUsername())
                        .get(0)
                        .getUuid()
                        .toString()
                )
                .build();
    }

    private Expense buildExpense() {
        return getDefaultExpenseBuilder()
                .build();
    }

    private Expense.ExpenseBuilder getDefaultExpenseBuilder() {
        return Expense.builder()
                .amount(BigDecimal.TEN)
                .description("description")
                .expenseDate(TODAY);
    }

    private LoginDto buildOnlyUserLoginDto() {
        return LoginDto.builder()
                .username(ONLY_USER)
                .password(PASSWORD)
                .build();
    }
}
