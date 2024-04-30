package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.model.ExpenseCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ApiTestConfig
class ExpenseCategoryResourceIT {
    private static final String EXPENSE_CATEGORY_USER_EXPENSE_CATEGORY = "expenseCategoryUserExpenseCategory";
    private static final String OTHER_USER = "otherUser";
    private static final String EXPENSE_CATEGORY_USER = "expenseCategoryUser";
    private static final String PASSWORD = "password";
    private static final String NEW_EXPENSE_CATEGORY_NAME = "newExpenseCategory";

    @Autowired
    private RestClientTestService restClientTestService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getExpenseCategories() {
        loginExpenseCategoryUser()
                .get().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ExpenseCategory.class)
                .hasSize(1)
                .value(v -> {
                    ExpenseCategory expenseCategory = v.get(0);
                    assertEquals(EXPENSE_CATEGORY_USER_EXPENSE_CATEGORY, expenseCategory.getName());
                    assertEquals(EXPENSE_CATEGORY_USER, expenseCategory.getUser().getUsername());
                    assertNull(expenseCategory.getUser().getPassword());
                    assertNotNull(expenseCategory.getUuid());
                });
    }
    private WebTestClient loginExpenseCategoryUser() {
        return this.restClientTestService.login(webTestClient, LoginDto.builder()
                .username(EXPENSE_CATEGORY_USER)
                .password(PASSWORD)
                .build());
    }
    @Test
    void getExpenseCategoriesUnauthorized() {
        this.webTestClient
                .get().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getExpenseCategoriesNoCategories() {
        this.restClientTestService
                .login(webTestClient, getUserWithoutCategories())
                .get().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ExpenseCategory.class)
                .hasSize(0);
    }

    private LoginDto getUserWithoutCategories() {
        return LoginDto.builder()
                .username(OTHER_USER)
                .password(PASSWORD)
                .build();
    }

    @Test
    void createExpenseCategory() {
        loginExpenseCategoryUser()
                .post().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES)
                .body(Mono.just(getNewExpenseCategory()), ExpenseCategory.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExpenseCategory.class)
                .value(v -> {
                    assertEquals(NEW_EXPENSE_CATEGORY_NAME, v.getName());
                    assertNotNull(v.getUuid());
                });
    }
    private ExpenseCategory getNewExpenseCategory() {
        return ExpenseCategory.builder()
                .name(NEW_EXPENSE_CATEGORY_NAME)
                .build();
    }

    @Test
    void createExpenseCategoryUnauthorized() {
        this.webTestClient
                .post().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES)
                .body(Mono.just(getNewExpenseCategory()), ExpenseCategory.class)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void createExpenseCategoryAlreadyExists() {
        loginExpenseCategoryUser()
                .post().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES)
                .body(Mono.just(getEncodedPasswordUserExpenseCategory()), ExpenseCategory.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    private ExpenseCategory getEncodedPasswordUserExpenseCategory() {
        return ExpenseCategory.builder()
                .name(EXPENSE_CATEGORY_USER_EXPENSE_CATEGORY)
                .build();
    }
}
