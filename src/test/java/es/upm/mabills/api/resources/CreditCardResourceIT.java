package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.services.TokenCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ApiTestConfig
class CreditCardResourceIT {
    private static final String ONLY_USER = "onlyUser";
    private static final String PASSWORD = "password";
    private static final String NEW_CREDIT_CARD_NUMBER = "0000000000000000";
    private static final String OTHER_CREDIT_CARD_NUMBER = "0000000000000001";
    private static final String OTHER_NEW_CREDIT_CARD_NUMBER = "1200000000000001";
    private static final String NOT_FOUND_BANK_ACCOUNT_UUID = "00000-0000-0000-0000-000000000001";
    private static final String NOT_FOUND_BANK_ACCOUNT_IBAN = "ES0000000000000000000000";
    private static final String TO_DELETE_CREDIT_CARD_NUMBER = "to_delete_credit_card_number";

    @Autowired
    private RestClientTestService restClientTestService;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TokenCacheService tokenCacheService;

    @BeforeEach
    void setUp() {
        when(tokenCacheService.isTokenBlackListed(anyString())).thenReturn(false);
    }

    @Test
    void getUserCreditCards() {
        restClientTestService
                .loginDefault(webTestClient)
                .get()
                .uri(CreditCardResource.CREDIT_CARDS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CreditCard.class)
                .value(creditCards -> assertFalse(creditCards.isEmpty()));
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
                .login(webTestClient, buildOnlyUserLoginDto())
                .get().uri(CreditCardResource.CREDIT_CARDS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CreditCard.class)
                .hasSize(0);
    }

    @Test
    void testCreateCreditCardSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(CreditCardResource.CREDIT_CARDS)
                .bodyValue(buildNewCreditCard())
                .exchange()
                .expectStatus().isOk()
                .expectBody(CreditCard.class)
                .value(creditCard -> assertEquals(NEW_CREDIT_CARD_NUMBER, creditCard.getCreditCardNumber()));
    }

    private CreditCard buildNewCreditCard() {
        return CreditCard.builder()
                .creditCardNumber(NEW_CREDIT_CARD_NUMBER)
                .build();
    }

    @Test
    void testCreateCreditCardUnauthorized() {
        webTestClient
                .post()
                .uri(CreditCardResource.CREDIT_CARDS)
                .bodyValue(buildNewCreditCard())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testCreateCreditCardBankAccountNotFound() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(CreditCardResource.CREDIT_CARDS)
                .bodyValue(buildBankAccountNotFoundCreditCard())
                .exchange()
                .expectStatus().isNotFound();
    }
    private CreditCard buildBankAccountNotFoundCreditCard() {
        return CreditCard.builder()
                .creditCardNumber(OTHER_CREDIT_CARD_NUMBER)
                .bankAccount(BankAccount.builder()
                        .uuid(NOT_FOUND_BANK_ACCOUNT_UUID)
                        .iban(NOT_FOUND_BANK_ACCOUNT_IBAN)
                        .build())
                .build();
    }

    @Test
    void testCreateCreditCardWithBankAccountSuccess() {
        String bankAccountUUID = getBankAccountUuid();
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(CreditCardResource.CREDIT_CARDS)
                .bodyValue(buildCreditCardWithBankAccount(bankAccountUUID))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CreditCard.class)
                .value(creditCard -> {
                    assertEquals(OTHER_NEW_CREDIT_CARD_NUMBER, creditCard.getCreditCardNumber());
                    assertNotNull(creditCard.getBankAccount());
                    assertEquals(bankAccountUUID, creditCard.getBankAccount().getUuid());
                });
    }

    @Test
    void testDeleteCreditCardSuccess() {
        String creditCardUuid = getToDeleteCreditCardUuidCreditCardUuid();
        restClientTestService
                .loginDefault(webTestClient)
                .delete()
                .uri(CreditCardResource.CREDIT_CARDS + CreditCardResource.DELETE_CREDIT_CARD, creditCardUuid)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testDeleteCreditCardUnauthorized() {
        webTestClient
                .delete()
                .uri(CreditCardResource.CREDIT_CARDS + CreditCardResource.DELETE_CREDIT_CARD, UUID.randomUUID())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testDeleteCreditCardForAnotherUser() {
        String otherCreditCardUuid = getEncodedPasswordUserFirstCreditCardUuid();
        restClientTestService
                .login(webTestClient, buildOnlyUserLoginDto())
                .delete()
                .uri(CreditCardResource.CREDIT_CARDS+CreditCardResource.DELETE_CREDIT_CARD, otherCreditCardUuid)
                .exchange()
                .expectStatus()
                .isNotFound();
    }



    private CreditCard buildCreditCardWithBankAccount(String uuid) {
        return CreditCard.builder()
                .creditCardNumber(OTHER_NEW_CREDIT_CARD_NUMBER)
                .bankAccount(BankAccount.builder()
                        .uuid(uuid)
                        .iban(NOT_FOUND_BANK_ACCOUNT_IBAN)
                        .build())
                .build();
    }

    private String getBankAccountUuid() {
        return Objects.requireNonNull(restClientTestService
                        .loginDefault(webTestClient)
                        .get()
                        .uri(BankAccountResource.BANK_ACCOUNTS)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBodyList(BankAccount.class)
                        .returnResult()
                        .getResponseBody())
                .get(0)
                .getUuid();
    }

    private LoginDto buildOnlyUserLoginDto() {
        return LoginDto.builder()
                .username(ONLY_USER)
                .password(PASSWORD)
                .build();
    }

    private String getToDeleteCreditCardUuidCreditCardUuid() {
        return getDefaultUserCreditCardStream()
                .filter(creditCard -> TO_DELETE_CREDIT_CARD_NUMBER.equals(creditCard.getCreditCardNumber()))
                .toList()
                .get(0)
                .getUuid();
    }

    private String getEncodedPasswordUserFirstCreditCardUuid() {
        return getDefaultUserCreditCardStream()
                .toList()
                .get(0)
                .getUuid();
    }

    private Stream<CreditCard> getDefaultUserCreditCardStream() {
        return Objects.requireNonNull(restClientTestService
                        .loginDefault(webTestClient)
                        .get()
                        .uri(CreditCardResource.CREDIT_CARDS)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBodyList(CreditCard.class)
                        .returnResult()
                        .getResponseBody())
                .stream();
    }
}
