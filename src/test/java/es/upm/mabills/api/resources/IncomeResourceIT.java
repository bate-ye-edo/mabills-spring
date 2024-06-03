package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.Income;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
import es.upm.mabills.persistence.repositories.CreditCardRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ApiTestConfig
class IncomeResourceIT {
    private static final LoginDto ONLY_USER_LOGIN = LoginDto.builder()
            .username("onlyUser")
            .password("password")
            .build();
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final Timestamp TODAY = new Timestamp(System.currentTimeMillis());
    private static final String BANK_ACCOUNT_IBAN_WITH_CREDIT_CARD = "ES004120003120034012";
    private static final String CREDIT_CARD_NUMBER_RELATED_TO_BANK_ACCOUNT = "004120012352345632";
    private static final UUID RANDOM_UUID = UUID.randomUUID();

    @Autowired
    private RestClientTestService restClientTestService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    private UserEntity encodedUserEntity;


    @BeforeEach
    void setUp() {
        encodedUserEntity = userRepository.findByUsername(ENCODED_PASSWORD_USER);
    }


    @Test
    void testGetUserIncomesEmpty() {
        restClientTestService.login(webTestClient, ONLY_USER_LOGIN)
                .get().uri(IncomeResource.INCOMES)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Income.class)
                .hasSize(0);
    }

    @Test
    void testGetUserIncomesSuccess() {
        restClientTestService.loginDefault(webTestClient)
                .get().uri(IncomeResource.INCOMES)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Income.class)
                .value(incomes -> assertFalse(incomes.isEmpty()));
    }

    @Test
    void testGetUserIncomesUnauthorized() {
        webTestClient
                .get().uri(IncomeResource.INCOMES)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testCreateIncomeSuccess() {
        Income income = buildIncomeWithoutDependencies();
        restClientTestService.loginDefault(webTestClient)
                .post().uri(IncomeResource.INCOMES)
                .bodyValue(income)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Income.class)
                .value(incomeResponse -> {
                    assertNotNull(incomeResponse.getUuid());
                    assertEquals(income.getAmount(), incomeResponse.getAmount());
                    assertEquals(income.getDescription(), incomeResponse.getDescription());
                    assertEquals(income.getIncomeDate(), incomeResponse.getIncomeDate());
                });
    }

    @Test
    void testCreateIncomeUnauthorized() {
        Income income = buildIncomeWithoutDependencies();
        webTestClient
                .post().uri(IncomeResource.INCOMES)
                .bodyValue(income)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testCreateIncomeBadRequest() {
        restClientTestService
                .loginDefault(webTestClient)
                .post().uri(IncomeResource.INCOMES)
                .bodyValue(Income.builder().build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateIncomeWithDependenciesThrowsCreditCardNotRelatedToBankAccount() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(IncomeResource.INCOMES)
                .bodyValue(buildIncomeWithDependenciesCreditCardNotRelatedToBankAccount())
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void testCreateIncomeWithDependenciesSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(IncomeResource.INCOMES)
                .bodyValue(buildIncomeWithDependencies())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Income.class)
                .value(expense -> {
                    assertNotNull(expense);
                    assertNotNull(expense.getAmount());
                    assertNotNull(expense.getCreditCard());
                    assertNotNull(expense.getBankAccount());
                });
    }

    @Test
    void testCreateIncomeBankAccountNotFound() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(IncomeResource.INCOMES)
                .bodyValue(buildIncomeBankAccountRandom())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testCreateIncomeCreditCardNotFound() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(IncomeResource.INCOMES)
                .bodyValue(buildIncomeCreditCardRandom())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    private Income buildIncomeWithoutDependencies() {
        return Income.builder()
                .amount(BigDecimal.TEN)
                .description("Income")
                .incomeDate(TODAY)
                .build();
    }

    private Income.IncomeBuilder getDefaultIncomeBuilder() {
        return Income.builder()
                .amount(BigDecimal.TEN)
                .description("description")
                .incomeDate(TODAY);
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

    private Income buildIncomeWithDependenciesCreditCardNotRelatedToBankAccount() {
        BankAccount bankAccount = buildBankAccount();
        return getDefaultIncomeBuilder()
                .bankAccount(bankAccount)
                .creditCard(buildCreditCard(bankAccount))
                .build();
    }

    private Income buildIncomeWithDependencies() {
        return getDefaultIncomeBuilder()
                .bankAccount(buildBankAccountWithCreditCard())
                .creditCard(buildCreditCardWithBankAccountRelated())
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


    private Income buildIncomeCreditCardRandom() {
        return getDefaultIncomeBuilder()
                .creditCard(buildRandomCreditCard())
                .build();
    }

    private CreditCard buildRandomCreditCard() {
        return CreditCard.builder()
                .uuid(RANDOM_UUID.toString())
                .build();
    }

    private Income buildIncomeBankAccountRandom() {
        return getDefaultIncomeBuilder()
                .bankAccount(buildRandomBankAccount())
                .build();
    }

    private BankAccount buildRandomBankAccount() {
        return BankAccount.builder()
                .uuid(RANDOM_UUID.toString())
                .build();
    }
}
