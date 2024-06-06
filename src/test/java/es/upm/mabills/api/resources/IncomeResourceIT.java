package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.Income;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.IncomeEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
import es.upm.mabills.persistence.repositories.CreditCardRepository;
import es.upm.mabills.persistence.repositories.IncomeRepository;
import es.upm.mabills.persistence.repositories.RepositorySort;
import es.upm.mabills.persistence.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
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
    private static final String TO_UPDATE_INCOME_CREDIT_CARD_NUMBER = "004120003120034012";
    private static final String TO_UPDATE_INCOME_RESOURCE_CREDIT_CARD_NUMBER = "004120003120034000";
    private static final String TO_DELETE_INCOME_RESOURCE = "to_delete_income_resource";
    private static final String ANOTHER_DESCRIPTION = "anotherDescription";

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

    @Autowired
    private IncomeRepository incomeRepository;

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
        Income income = buildIncomeWithDependenciesCreditCardNotRelatedToBankAccount();
        LogManager.getLogger(IncomeResourceIT.class).info(income);
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(IncomeResource.INCOMES)
                .bodyValue(income)
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
                .value(income -> {
                    assertNotNull(income);
                    assertNotNull(income.getAmount());
                    assertNotNull(income.getCreditCard());
                    assertNotNull(income.getBankAccount());
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

    @Test
    void testUpdateIncomeSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .put()
                .uri(IncomeResource.INCOMES)
                .bodyValue(buildIncomeToUpdate())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Income.class)
                .value(income -> {
                    assertNotNull(income);
                    assertNotNull(income.getUuid());
                    assertNotNull(income.getIncomeDate());
                    assertNotNull(income.getAmount());
                    assertNotNull(income.getDescription());
                });
    }

    @Test
    void testUpdateIncomeNotFoundBankAccount() {
        restClientTestService
                .loginDefault(webTestClient)
                .put()
                .uri(IncomeResource.INCOMES)
                .bodyValue(buildIncomeToUpdateNotFoundBankAccount())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testUpdateIncomeNotFoundCreditCard() {
        restClientTestService
                .loginDefault(webTestClient)
                .put()
                .uri(IncomeResource.INCOMES)
                .bodyValue(buildIncomeToUpdateNotFoundCreditCard())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testUpdateIncomeUnauthorized() {
        webTestClient
                .put()
                .uri(IncomeResource.INCOMES)
                .bodyValue(buildIncome())
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @Transactional
    void testUpdateIncomeWithAllDependenciesSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .put()
                .uri(IncomeResource.INCOMES)
                .bodyValue(buildIncomeToUpdateWithAllDependencies())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Income.class)
                .value(income -> {
                    assertNotNull(income);
                    assertNotNull(income.getAmount());
                    assertNotNull(income.getCreditCard());
                });
    }

    @Test
    void testUpdateIncomeIncomeNotFound() {
        restClientTestService
                .loginDefault(webTestClient)
                .put()
                .uri(IncomeResource.INCOMES)
                .bodyValue(buildIncomeToUpdateNotFound())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testDeleteIncomeSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .delete()
                .uri(IncomeResource.INCOMES + IncomeResource.UUID, findIncomeToDelete().getUuid())
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void testDeleteIncomeUnauthorized() {
        webTestClient
                .delete()
                .uri(IncomeResource.INCOMES + IncomeResource.UUID, findIncomeToDelete().getUuid())
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void testDeleteIncomeNotFound() {
        restClientTestService
                .loginDefault(webTestClient)
                .delete()
                .uri(IncomeResource.INCOMES + IncomeResource.UUID, UUID.randomUUID())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    private Income buildIncome() {
        return getDefaultIncomeBuilder()
                .build();
    }
    
    private IncomeEntity findIncomeToDelete() {
        return incomeRepository.findByUserId(encodedUserEntity.getId(), RepositorySort.BY_CREATION_DATE.value())
                .stream()
                .filter(incomeEntity -> incomeEntity.getDescription().equals(TO_DELETE_INCOME_RESOURCE))
                .findFirst()
                .orElseThrow();
    }

    private Income buildIncomeToUpdateNotFound() {
        return Income.builder()
                .uuid(UUID.randomUUID().toString())
                .amount(BigDecimal.TEN)
                .incomeDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .build();
    }

    private Income buildIncomeToUpdate() {
        return Income.builder()
                .uuid(findIncomeToUpdate(TO_UPDATE_INCOME_CREDIT_CARD_NUMBER).getUuid().toString())
                .amount(BigDecimal.TEN)
                .incomeDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .build();
    }

    private Income buildIncomeToUpdateWithAllDependencies() {
        return Income.builder()
                .uuid(findIncomeWithDependenciesToUpdate().getUuid().toString())
                .amount(BigDecimal.TEN)
                .incomeDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .creditCard(buildCreditCardEntityReference())
                .build();
    }

    private CreditCard buildCreditCardEntityReference() {
        return CreditCard.builder()
                .uuid(encodedUserEntity.getCreditCards().get(0).getUuid().toString())
                .creditCardNumber(encodedUserEntity.getCreditCards().get(0).getCreditCardNumber())
                .build();
    }

    private IncomeEntity findIncomeWithDependenciesToUpdate() {
        return incomeRepository.findByUserId(encodedUserEntity.getId(), RepositorySort.BY_CREATION_DATE.value())
                .stream()
                .filter(incomeEntity -> incomeEntity.getAmount().compareTo(BigDecimal.TEN) == 0
                        && incomeEntity.getCreditCard() != null && incomeEntity.getCreditCard().getCreditCardNumber().equals(TO_UPDATE_INCOME_CREDIT_CARD_NUMBER))
                .findFirst()
                .orElseThrow();
    }

    private Income buildIncomeToUpdateNotFoundBankAccount() {
        return Income.builder()
                .uuid(findIncomeToUpdate(TO_UPDATE_INCOME_CREDIT_CARD_NUMBER).getUuid().toString())
                .amount(BigDecimal.TEN)
                .incomeDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .bankAccount(BankAccount.builder().uuid(UUID.randomUUID().toString()).build())
                .build();
    }

    private Income buildIncomeToUpdateNotFoundCreditCard() {
        return Income.builder()
                .uuid(findIncomeToUpdate(TO_UPDATE_INCOME_RESOURCE_CREDIT_CARD_NUMBER).getUuid().toString())
                .amount(BigDecimal.TEN)
                .incomeDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(ANOTHER_DESCRIPTION)
                .creditCard(CreditCard.builder().uuid(UUID.randomUUID().toString()).build())
                .build();
    }

    private IncomeEntity findIncomeToUpdate(String creditCardNumber) {
        return incomeRepository.findByUserId(encodedUserEntity.getId(), RepositorySort.BY_CREATION_DATE.value())
                .stream()
                .filter(incomeEntity -> incomeEntity.getCreditCard() != null && incomeEntity.getCreditCard().getCreditCardNumber().equals(creditCardNumber))
                .findFirst()
                .orElseThrow();
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
                                && creditCardEntity.getBankAccount().getUuid().compareTo(UUID.fromString(bankAccount.getUuid())) != 0)
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
