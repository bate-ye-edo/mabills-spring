package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.model.BankAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

@ApiTestConfig
class BankAccountResourceIT {
    private static final String ONLY_USER = "onlyUser";
    private static final String PASSWORD = "password";

    @Autowired
    private RestClientTestService restClientTestService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getUserBankAccounts() {
        restClientTestService
                .loginDefault(webTestClient)
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
                .login(webTestClient, getOnlyUserLoginDto())
                .get().uri(BankAccountResource.BANK_ACCOUNTS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BankAccount.class)
                .hasSize(0);
    }

    private LoginDto getOnlyUserLoginDto() {
        return LoginDto.builder()
                .username(ONLY_USER)
                .password(PASSWORD)
                .build();
    }
}