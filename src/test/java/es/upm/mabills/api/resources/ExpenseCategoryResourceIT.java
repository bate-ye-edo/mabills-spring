package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.model.ExpenseCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ApiTestConfig
class ExpenseCategoryResourceIT {
    private static final String ENCODED_PASSWORD_USER_EXPENSE_CATEGORY = "encodedPasswordUserExpenseCategory";
    private static final String OTHER_USER = "otherUser";
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String PASSWORD = "password";
    @Autowired
    private RestClientTestService restClientTestService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getExpenseCategories() {
        this.restClientTestService
                .loginDefault(webTestClient)
                .get().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ExpenseCategory.class)
                .hasSize(1)
                .value(v -> {
                    ExpenseCategory expenseCategory = v.get(0);
                    assertEquals(ENCODED_PASSWORD_USER_EXPENSE_CATEGORY, expenseCategory.getName());
                    assertEquals(ENCODED_PASSWORD_USER, expenseCategory.getUser().getUsername());
                    assertNull(expenseCategory.getUser().getPassword());
                    assertNotNull(expenseCategory.getUuid());
                });
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


}
