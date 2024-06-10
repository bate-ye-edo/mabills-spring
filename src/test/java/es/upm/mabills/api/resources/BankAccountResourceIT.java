package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.mappers.BankAccountMapper;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
import es.upm.mabills.persistence.repositories.UserRepository;
import es.upm.mabills.services.TokenCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ApiTestConfig
class BankAccountResourceIT {
    private static final String ONLY_USER = "onlyUser";
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String PASSWORD = "password";
    private static final String OTHER_USER = "otherUser";
    private static final String NEW_IBAN = "ES7921000813610123456789";
    private static final String EXIST_IBAN = "ES004120003120034012";
    private static final String TO_DELETE_BANK_ACCOUNT = "to_delete_bank_account";
    private static final String TO_DELETE_IBAN_WITH_CREDIT_CARD = "to_delete_bank_account_entity_with_credit_card";
    private static final String TO_DELETE_IBAN_WITH_CREDIT_CARD_AND_EXPENSE = "to_delete_bank_account_entity_with_credit_card_and_expense";
    private static final String CREDIT_CARD_NUMBER_WITH_DELETED_BANK_ACCOUNT = "bank_account_will_be_deleted";
    private static final String CREDIT_CARD_NUMBER_WITH_DELETED_BANK_ACCOUNT_AND_EXPENSE = "bank_account_will_be_deleted_and_expense";

    @Autowired
    private RestClientTestService restClientTestService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankAccountMapper bankAccountMapper;

    @MockBean
    private TokenCacheService tokenCacheService;

    private UserEntity encodedPasswordUser;

    @BeforeEach
    void setUp() {
        when(tokenCacheService.isTokenBlackListed(anyString())).thenReturn(false);
        encodedPasswordUser = userRepository.findByUsername(ENCODED_PASSWORD_USER);
    }


    @Test
    void getUserBankAccounts() {
        restClientTestService
                .login(webTestClient, buildOtherUserLoginDto())
                .get()
                .uri(BankAccountResource.BANK_ACCOUNTS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BankAccount.class)
                .hasSize(1);
    }

    @Test
    void getUserBankAccountsUnauthorized() {
        webTestClient
                .get()
                .uri(BankAccountResource.BANK_ACCOUNTS)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getUserBankAccountsEmpty() {
        restClientTestService
                .login(webTestClient, buildOnlyUserLoginDto())
                .get().uri(BankAccountResource.BANK_ACCOUNTS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BankAccount.class)
                .hasSize(0);
    }

    @Test
    void testCreateBankAccountSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(BankAccountResource.BANK_ACCOUNTS)
                .bodyValue(BankAccount.builder().iban(NEW_IBAN).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(BankAccount.class)
                .value(bankAccount -> assertEquals(NEW_IBAN, bankAccount.getIban()));
    }

    @Test
    void testCreateBankAccountUnauthorized() {
        webTestClient
                .post()
                .uri(BankAccountResource.BANK_ACCOUNTS)
                .bodyValue(BankAccount.builder().iban(NEW_IBAN).build())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testCreateBankAccountInvalidIban() {
        restClientTestService
                .login(webTestClient, buildOnlyUserLoginDto())
                .post()
                .uri(BankAccountResource.BANK_ACCOUNTS)
                .bodyValue(BankAccount.builder().iban("invalid").build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateBankAccountAlreadyExists() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(BankAccountResource.BANK_ACCOUNTS)
                .bodyValue(BankAccount.builder().iban(EXIST_IBAN).build())
                .exchange()
                .expectStatus()
                .isEqualTo(409);
    }

    @Test
    @Transactional
    void testDeleteBankAccountSuccess() {
        String toDeleteUuid = getToDeleteBankAccountUuid();
        restClientTestService
                .loginDefault(webTestClient)
                .delete()
                .uri(BankAccountResource.BANK_ACCOUNTS + BankAccountResource.UUID, toDeleteUuid)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    @Transactional
    void testDeleteBankAccountForAnotherUserException() {
        String otherUuid = getDefaultUserFirstBankAccountUuid();
        restClientTestService
                .login(webTestClient, buildOnlyUserLoginDto())
                .delete()
                .uri(BankAccountResource.BANK_ACCOUNTS + BankAccountResource.UUID, otherUuid)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @Transactional
    void testDeleteBankAccountWithCreditCard() {
        String toDeleteBankAccountUuid = getBankAccountWithCreditCardUuid();
        restClientTestService
                .loginDefault(webTestClient)
                .delete()
                .uri(BankAccountResource.BANK_ACCOUNTS + BankAccountResource.UUID, toDeleteBankAccountUuid)
                .exchange()
                .expectStatus()
                .isOk();
        assertCreditCardDoesNotHasBankAccount(CREDIT_CARD_NUMBER_WITH_DELETED_BANK_ACCOUNT);
    }

    @Test
    @Transactional
    void testDeleteBankAccountWithCreditCardAndExpense() {
        String toDeleteBankAccountUuid = getBankAccountWithCreditCardAndExpenseUuid();
        restClientTestService
                .loginDefault(webTestClient)
                .delete()
                .uri(BankAccountResource.BANK_ACCOUNTS + BankAccountResource.UUID, toDeleteBankAccountUuid)
                .exchange()
                .expectStatus()
                .isOk();
        assertCreditCardDoesNotHasBankAccount(CREDIT_CARD_NUMBER_WITH_DELETED_BANK_ACCOUNT_AND_EXPENSE);
    }


    private void assertCreditCardDoesNotHasBankAccount(String creditCardNumber) {
        CreditCard cd = restClientTestService
                .loginDefault(webTestClient)
                .get()
                .uri(CreditCardResource.CREDIT_CARDS)
                .exchange()
                .returnResult(CreditCard.class)
                .getResponseBody()
                .filter(creditCard -> creditCard.getCreditCardNumber().equals(creditCardNumber))
                .toStream()
                .toList()
                .get(0);
        assertNull(cd.getBankAccount());
    }

    private String getBankAccountWithCreditCardUuid() {
        return getDefaultUserBankAccountsStream()
                .filter(ba -> ba.getIban().equals(TO_DELETE_IBAN_WITH_CREDIT_CARD))
                .toList()
                .get(0)
                .getUuid();
    }

    private String getBankAccountWithCreditCardAndExpenseUuid() {
        return getDefaultUserBankAccountsStream()
                .filter(ba -> ba.getIban().equals(TO_DELETE_IBAN_WITH_CREDIT_CARD_AND_EXPENSE))
                .toList()
                .get(0)
                .getUuid();
    }

    private String getToDeleteBankAccountUuid() {
        return getDefaultUserBankAccountsStream()
                .filter(ba -> ba.getIban().equals(TO_DELETE_BANK_ACCOUNT))
                .toList()
                .get(0)
                .getUuid();
    }

    private String getDefaultUserFirstBankAccountUuid() {
        return getDefaultUserBankAccountsStream()
                .toList()
                .get(0)
                .getUuid();
    }

    private Stream<BankAccount> getDefaultUserBankAccountsStream() {
        return Objects.requireNonNull(bankAccountRepository.findByUserId(encodedPasswordUser.getId()))
                .stream().map(bankAccountMapper::toBankAccount);
    }

    private LoginDto buildOnlyUserLoginDto() {
        return LoginDto.builder()
                .username(ONLY_USER)
                .password(PASSWORD)
                .build();
    }

    private LoginDto buildOtherUserLoginDto() {
        return LoginDto.builder()
                .username(OTHER_USER)
                .password(PASSWORD)
                .build();
    }
}
