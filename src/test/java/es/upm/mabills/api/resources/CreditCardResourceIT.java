package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.model.CreditCard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

@ApiTestConfig
class CreditCardResourceIT {
    private static final String ONLY_USER = "onlyUser";
    private static final String PASSWORD = "password";

    @Autowired
    private RestClientTestService restClientTestService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getUserCreditCards() {
        restClientTestService
                .loginDefault(webTestClient)
                .get()
                .uri(CreditCardResource.CREDIT_CARDS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CreditCard.class)
                .hasSize(1);
    }

    @Test
    void getUserCreditCardsUnauthorized() {
        webTestClient
                .get()
                .uri(CreditCardResource.CREDIT_CARDS)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getUserCreditCardsEmpty() {
        restClientTestService
                .login(webTestClient, getOnlyUserLoginDto())
                .get().uri(CreditCardResource.CREDIT_CARDS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CreditCard.class)
                .hasSize(0);
    }

    private LoginDto getOnlyUserLoginDto() {
        return LoginDto.builder()
                .username(ONLY_USER)
                .password(PASSWORD)
                .build();
    }
}