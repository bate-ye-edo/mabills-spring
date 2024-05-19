package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.model.BankAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ApiTestConfig
class BankAccountResourceIT {
    private static final String ONLY_USER = "onlyUser";
    private static final String PASSWORD = "password";
    private static final String OTHER_USER = "otherUser";

    private static final String NEW_IBAN = "ES7921000813610123456789";

    private static final String EXIST_IBAN = "ES004120003120034012";

    @Autowired
    private RestClientTestService restClientTestService;

    @Autowired
    private WebTestClient webTestClient;

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
