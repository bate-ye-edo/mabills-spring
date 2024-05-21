package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.model.Expense;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ApiTestConfig
class ExpenseResourceIT {
    private static final String ONLY_USER = "onlyUser";
    private static final String PASSWORD = "password";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

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

    private LoginDto buildOnlyUserLoginDto() {
        return LoginDto.builder()
                .username(ONLY_USER)
                .password(PASSWORD)
                .build();
    }
}
