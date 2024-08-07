package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.api.dtos.UpdateExpenseCategoryDto;
import es.upm.mabills.mappers.ExpenseCategoryMapper;
import es.upm.mabills.model.ExpenseCategory;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.ExpenseCategoryRepository;
import es.upm.mabills.persistence.repositories.UserRepository;
import es.upm.mabills.services.TokenCacheService;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ApiTestConfig
class ExpenseCategoryResourceIT {
    private static final String EXPENSE_CATEGORY_USER_EXPENSE_CATEGORY = "expenseCategoryUserExpenseCategory";
    private static final String TO_UPDATE_EXPENSE_CATEGORY = "toUpdateExpenseCategory";
    private static final String TO_DELETE_EXPENSE_CATEGORY = "toDeleteExpenseCategory";
    private static final String TO_DELETE_EXPENSE_CATEGORY_WITH_EXPENSE = "toDeleteExpenseCategoryWithExpense";
    private static final String UPDATED_EXPENSE_CATEGORY = "updatedExpenseCategory";
    private static final String ONLY_USER = "onlyUser";
    private static final String EXPENSE_CATEGORY_USER = "expenseCategoryUser";
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String PASSWORD = "password";
    private static final String NEW_EXPENSE_CATEGORY_NAME = "newExpenseCategory";
    private static final String TEST_UUID = UUID.randomUUID().toString();
    @Autowired
    private RestClientTestService restClientTestService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Autowired
    private ExpenseCategoryMapper expenseCategoryMapper;

    private UserEntity encodedPasswordUser;

    @MockBean
    private TokenCacheService tokenCacheService;

    @BeforeEach
    void setUp() {
        encodedPasswordUser = userRepository.findByUsername(ENCODED_PASSWORD_USER);
        when(tokenCacheService.isTokenBlackListed(any())).thenReturn(false);
    }

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
                .username(ONLY_USER)
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

    @Test
    void updateExpenseCategoryName() {
        ExpenseCategory expenseCategory = createExpenseCategoryToUse(getToUpdateExpenseCategory());
        loginExpenseCategoryUser()
                .put().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES  + ExpenseCategoryResource.UUID + ExpenseCategoryResource.NAME, expenseCategory.getUuid())
                .body(Mono.just(getUpdateExpenseCategoryDto()), UpdateExpenseCategoryDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExpenseCategory.class)
                .value(v -> assertEquals(UPDATED_EXPENSE_CATEGORY, v.getName()));
    }

    private ExpenseCategory createExpenseCategoryToUse(ExpenseCategory expenseCategory) {
        return loginExpenseCategoryUser()
                .post().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES)
                .body(Mono.just(expenseCategory), ExpenseCategory.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExpenseCategory.class)
                .returnResult()
                .getResponseBody();
    }

    private ExpenseCategory getToUpdateExpenseCategory() {
        return ExpenseCategory.builder()
                .name(TO_UPDATE_EXPENSE_CATEGORY)
                .build();
    }

    private UpdateExpenseCategoryDto getUpdateExpenseCategoryDto() {
        return UpdateExpenseCategoryDto.builder()
                .name(UPDATED_EXPENSE_CATEGORY)
                .build();
    }

    @Test
    void updateExpenseCategoryNameUnauthorized() {
        this.webTestClient
                .put().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES + ExpenseCategoryResource.UUID + ExpenseCategoryResource.NAME, TEST_UUID)
                .body(Mono.just(getUpdateExpenseCategoryDto()), UpdateExpenseCategoryDto.class)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void updateExpenseCategoryNameNotFound() {
        loginExpenseCategoryUser()
                .put().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES + ExpenseCategoryResource.UUID + ExpenseCategoryResource.NAME, TEST_UUID)
                .body(Mono.just(getUpdateExpenseCategoryDto()), UpdateExpenseCategoryDto.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateExpenseCategoryNameBadRequest() {
        loginExpenseCategoryUser()
                .put().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES + ExpenseCategoryResource.UUID + ExpenseCategoryResource.NAME, TEST_UUID)
                .body(Mono.just(UpdateExpenseCategoryDto.builder().build()), UpdateExpenseCategoryDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void deleteExpenseCategory() {
        ExpenseCategory expenseCategory = createExpenseCategoryToUse(getToDeleteExpenseCategory());
        loginExpenseCategoryUser()
                .delete().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES + ExpenseCategoryResource.UUID, expenseCategory.getUuid())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteExpenseCategoryWithExpense() {
        ExpenseCategory expenseCategory = getToDeleteExpenseCategoryWithExpense();
        LogManager.getLogger(ExpenseCategoryResourceIT.class).info(expenseCategory);
        restClientTestService
                .loginDefault(webTestClient)
                .delete()
                .uri(ExpenseCategoryResource.EXPENSE_CATEGORIES + ExpenseCategoryResource.UUID, expenseCategory.getUuid())
                .exchange()
                .expectStatus().isOk();
    }

    private ExpenseCategory getToDeleteExpenseCategoryWithExpense() {
        return expenseCategoryRepository.findByUser_Username(encodedPasswordUser.getUsername())
                .stream()
                .filter(e -> e.getName().equals(TO_DELETE_EXPENSE_CATEGORY_WITH_EXPENSE))
                .findFirst()
                .map(expenseCategoryMapper::toExpenseCategory)
                .orElse(null);
    }

    @Test
    void deleteExpenseCategoryUnauthorized() {
        this.webTestClient
                .delete().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES + ExpenseCategoryResource.UUID, TEST_UUID)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void deleteExpenseCategoryNotFound() {
        loginExpenseCategoryUser()
                .delete().uri(ExpenseCategoryResource.EXPENSE_CATEGORIES + ExpenseCategoryResource.UUID, TEST_UUID)
                .exchange()
                .expectStatus().isNotFound();
    }

    private ExpenseCategory getToDeleteExpenseCategory() {
        return ExpenseCategory.builder()
                .name(TO_DELETE_EXPENSE_CATEGORY)
                .build();
    }
}
