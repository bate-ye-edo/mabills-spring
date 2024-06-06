package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.model.FormOfPayment;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
import es.upm.mabills.persistence.repositories.CreditCardRepository;
import es.upm.mabills.persistence.repositories.ExpenseCategoryRepository;
import es.upm.mabills.persistence.repositories.ExpenseRepository;
import es.upm.mabills.persistence.repositories.RepositorySort;
import es.upm.mabills.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    private static final String TO_UPDATE_EXPENSE_CREDIT_CARD_NUMBER = "004120003120034012";
    private static final String TO_DELETE_EXPENSE_RESOURCE = "to_delete_expense_resource";
    private static final String ANOTHER_DESCRIPTION = "anotherDescription";

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

    @Autowired
    private ExpenseRepository expenseRepository;

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

    @Test
    void testUpdateExpenseSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .put()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(buildExpenseToUpdate())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Expense.class)
                .value(expense -> {
                    assertNotNull(expense);
                    assertNotNull(expense.getUuid());
                    assertNotNull(expense.getCreationDate());
                    assertNotNull(expense.getExpenseDate());
                    assertNotNull(expense.getAmount());
                    assertNotNull(expense.getDescription());
                    assertNotNull(expense.getFormOfPayment());
                });
    }

    @Test
    void testUpdateExpenseNotFoundBankAccount() {
        restClientTestService
                .loginDefault(webTestClient)
                .put()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(buildExpenseToUpdateNotFoundBankAccount())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testUpdateExpenseNotFoundCreditCard() {
        restClientTestService
                .loginDefault(webTestClient)
                .put()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(buildExpenseToUpdateNotFoundCreditCard())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testUpdateExpenseNotFoundExpenseCategory() {
        restClientTestService
                .loginDefault(webTestClient)
                .put()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(buildExpenseToUpdateNotFoundExpenseCategory())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testUpdateExpenseUnauthorized() {
        webTestClient
                .put()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(buildExpense())
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @Transactional
    void testUpdateExpenseWithAllDependenciesSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .put()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(buildExpenseToUpdateWithAllDependencies())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Expense.class)
                .value(expense -> {
                    assertNotNull(expense);
                    assertNotNull(expense.getExpenseCategory());
                    assertNotNull(expense.getAmount());
                    assertNotNull(expense.getCreditCard());
                });
    }

    @Test
    void testUpdateExpenseExpenseNotFound() {
        restClientTestService
                .loginDefault(webTestClient)
                .put()
                .uri(ExpenseResource.EXPENSES)
                .bodyValue(buildExpenseToUpdateNotFound())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testDeleteExpenseSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .delete()
                .uri(ExpenseResource.EXPENSES + ExpenseResource.UUID, findExpenseToDelete().getUuid())
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void testDeleteExpenseUnauthorized() {
        webTestClient
                .delete()
                .uri(ExpenseResource.EXPENSES + ExpenseResource.UUID, findExpenseToDelete().getUuid())
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void testDeleteExpenseNotFound() {
        restClientTestService
                .loginDefault(webTestClient)
                .delete()
                .uri(ExpenseResource.EXPENSES + ExpenseResource.UUID, UUID.randomUUID())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testChartResource() {
        restClientTestService
                .loginDefault(webTestClient)
                .get()
                .uri(ChartResource.CHARTS + ChartResource.DATA_TYPE, "EXPENSES")
                .exchange()
                .expectStatus()
                .isOk();
    }

    private ExpenseEntity findExpenseToDelete() {
        return expenseRepository.findByUserId(encodedUserEntity.getId(), RepositorySort.BY_CREATION_DATE.value())
                .stream()
                .filter(expenseEntity -> expenseEntity.getDescription().equals(TO_DELETE_EXPENSE_RESOURCE))
                .findFirst()
                .orElseThrow();
    }

    private Expense buildExpenseToUpdateNotFound() {
        return Expense.builder()
                .uuid(UUID.randomUUID().toString())
                .amount(BigDecimal.TEN)
                .expenseDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .formOfPayment(FormOfPayment.CASH)
                .build();
    }

    private Expense buildExpenseToUpdate() {
        return Expense.builder()
                .uuid(findExpenseToUpdate().getUuid().toString())
                .amount(BigDecimal.TEN)
                .expenseDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .formOfPayment(FormOfPayment.CASH)
                .build();
    }

    private Expense buildExpenseToUpdateWithAllDependencies() {
        return Expense.builder()
                .uuid(findExpenseWithDependenciesToUpdate().getUuid().toString())
                .amount(BigDecimal.TEN)
                .expenseDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .formOfPayment(FormOfPayment.CASH)
                .creditCard(buildCreditCardEntityReference())
                .expenseCategory(buildExpenseCategoryEntity())
                .build();
    }

    private ExpenseEntity findExpenseWithDependenciesToUpdate() {
        return expenseRepository.findByUserId(encodedUserEntity.getId(), RepositorySort.BY_CREATION_DATE.value())
                .stream()
                .filter(expenseEntity -> expenseEntity.getAmount().compareTo(BigDecimal.TEN) == 0
                        && expenseEntity.getCreditCard() != null && expenseEntity.getCreditCard().getCreditCardNumber().equals(TO_UPDATE_EXPENSE_CREDIT_CARD_NUMBER))
                .findFirst()
                .orElseThrow();
    }

    private CreditCard buildCreditCardEntityReference() {
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

    private Expense buildExpenseToUpdateNotFoundBankAccount() {
        return Expense.builder()
                .uuid(findExpenseToUpdate().getUuid().toString())
                .amount(BigDecimal.TEN)
                .expenseDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .formOfPayment(FormOfPayment.CASH)
                .bankAccount(BankAccount.builder().uuid(UUID.randomUUID().toString()).build())
                .build();
    }

    private Expense buildExpenseToUpdateNotFoundCreditCard() {
        return Expense.builder()
                .uuid(findExpenseToUpdate().getUuid().toString())
                .amount(BigDecimal.TEN)
                .expenseDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .formOfPayment(FormOfPayment.CASH)
                .creditCard(CreditCard.builder().uuid(UUID.randomUUID().toString()).build())
                .build();
    }

    private Expense buildExpenseToUpdateNotFoundExpenseCategory() {
        return Expense.builder()
                .uuid(findExpenseToUpdate().getUuid().toString())
                .amount(BigDecimal.TEN)
                .expenseDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .formOfPayment(FormOfPayment.CASH)
                .expenseCategory(ExpenseCategory.builder().uuid(UUID.randomUUID().toString()).build())
                .build();
    }

    private ExpenseEntity findExpenseToUpdate() {
        return expenseRepository.findByUserId(encodedUserEntity.getId(), RepositorySort.BY_CREATION_DATE.value())
                .stream()
                .filter(expenseEntity -> expenseEntity.getCreditCard() != null && expenseEntity.getCreditCard().getCreditCardNumber().equals(TO_UPDATE_EXPENSE_CREDIT_CARD_NUMBER))
                .findFirst()
                .orElseThrow();
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
        BankAccount bankAccount = buildBankAccount();
        return getDefaultExpenseBuilder()
                .bankAccount(bankAccount)
                .creditCard(buildCreditCard(bankAccount))
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

    private CreditCard buildCreditCard(BankAccount bankAccount) {
        return CreditCard.builder()
                .uuid(creditCardRepository.findByUserId(encodedUserEntity.getId(), RepositorySort.BY_CREATION_DATE.value())
                        .stream()
                        .filter(creditCardEntity -> Objects.nonNull(creditCardEntity.getBankAccount())
                                && !creditCardEntity.getBankAccount().getIban().equals(bankAccount.getIban()))
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
