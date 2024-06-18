package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.FilterChartDto;
import es.upm.mabills.api.dtos.FilterDto;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.filters.FilterComparisons;
import es.upm.mabills.model.filters.FilterField;
import es.upm.mabills.services.TokenCacheService;
import es.upm.mabills.services.charts.ChartCategory;
import es.upm.mabills.services.charts.ExpenseChartGroupBy;
import es.upm.mabills.services.charts.IncomeChartGroupBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ApiTestConfig
class ChartResourceIT {
    private static final String TODAY_STRING = new SimpleDateFormat("yyyy-MM-dd")
        .format(new Timestamp(System.currentTimeMillis()));

    private static final String RETURNED_TODAY_STRING = new SimpleDateFormat("dd-MM-yyyy")
            .format(new Timestamp(System.currentTimeMillis()));
    private static final LoginDto OTHER_USER_LOGIN = LoginDto.builder()
            .username("otherUser")
            .password("password")
            .build();

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @MockBean
    private TokenCacheService tokenCacheService;

    @BeforeEach
    void setUp() {
        when(tokenCacheService.isTokenBlackListed(anyString())).thenReturn(false);
    }

    @Test
    void testGetExpensesChartSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .get()
                .uri(ChartResource.CHARTS + ChartResource.CHART_CATEGORY, ChartCategory.EXPENSES)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Chart.class)
                .value(chart -> {
                    assertNotNull(chart);
                    assertNotNull(chart.getData());
                    assertFalse(chart.getData().isEmpty());
                });
    }

    @Test
    void testGetIncomesChartSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .get()
                .uri(ChartResource.CHARTS + ChartResource.CHART_CATEGORY, ChartCategory.INCOMES)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Chart.class)
                .value(chart -> {
                    assertNotNull(chart);
                    assertNotNull(chart.getData());
                    assertFalse(chart.getData().isEmpty());
                });
    }

    @Test
    void testGetChartInvalidDataType() {
        restClientTestService
                .loginDefault(webTestClient)
                .get()
                .uri(ChartResource.CHARTS + ChartResource.CHART_CATEGORY, "INVALID")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetChartUnauthorized() {
        webTestClient
                .get()
                .uri(ChartResource.CHARTS + ChartResource.CHART_CATEGORY, ChartCategory.EXPENSES)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testGetExpensesChartEmpty() {
        restClientTestService
                .login(webTestClient, OTHER_USER_LOGIN)
                .get()
                .uri(ChartResource.CHARTS + ChartResource.CHART_CATEGORY, ChartCategory.EXPENSES)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Chart.class)
                .value(chart -> {
                    assertNotNull(chart);
                    assertNotNull(chart.getData());
                    assertTrue(chart.getData().isEmpty());
                });
    }

    @Test
    void testGetIncomesChartEmpty() {
        restClientTestService
                .login(webTestClient, OTHER_USER_LOGIN)
                .get()
                .uri(ChartResource.CHARTS + ChartResource.CHART_CATEGORY, ChartCategory.INCOMES)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Chart.class)
                .value(chart -> {
                    assertNotNull(chart);
                    assertNotNull(chart.getData());
                    assertTrue(chart.getData().isEmpty());
                });
    }

    @Test
    void testGetExpensesChartGroupByCategorySuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .get()
                .uri(ChartResource.CHARTS + ChartResource.CHART_CATEGORY + ChartResource.CHART_GROUP_BY_TYPE, ChartCategory.EXPENSES, ExpenseChartGroupBy.EXPENSE_CATEGORY)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Chart.class)
                .value(chart -> {
                    assertNotNull(chart);
                    assertNotNull(chart.getData());
                    assertFalse(chart.getData().isEmpty());
                });
    }

    @Test
    void testGetExpensesChartGroupByDateSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .get()
                .uri(ChartResource.CHARTS + ChartResource.CHART_CATEGORY + ChartResource.CHART_GROUP_BY_TYPE, ChartCategory.EXPENSES, ExpenseChartGroupBy.EXPENSE_DATE)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Chart.class)
                .value(chart -> {
                    assertNotNull(chart);
                    assertNotNull(chart.getData());
                    assertFalse(chart.getData().isEmpty());
                });
    }

    @Test
    void testGetExpensesChartGroupByEmpty() {
        restClientTestService
                .login(webTestClient, OTHER_USER_LOGIN)
                .get()
                .uri(ChartResource.CHARTS + ChartResource.CHART_CATEGORY + ChartResource.CHART_GROUP_BY_TYPE, ChartCategory.EXPENSES, ExpenseChartGroupBy.EXPENSE_CATEGORY)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Chart.class)
                .value(chart -> {
                    assertNotNull(chart);
                    assertNotNull(chart.getData());
                    assertTrue(chart.getData().isEmpty());
                });
    }

    @Test
    void testGetExpensesChartGroupByInvalidDataType() {
        restClientTestService
                .loginDefault(webTestClient)
                .get()
                .uri(ChartResource.CHARTS + ChartResource.CHART_CATEGORY + ChartResource.CHART_GROUP_BY_TYPE, "INVALID", ExpenseChartGroupBy.EXPENSE_CATEGORY)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetExpensesChartGroupByInvalidGroupBy() {
        restClientTestService
                .loginDefault(webTestClient)
                .get()
                .uri(ChartResource.CHARTS + ChartResource.CHART_CATEGORY + ChartResource.CHART_GROUP_BY_TYPE, ChartCategory.EXPENSES, "INVALID")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetExpensesChartGroupByUnauthorized() {
        webTestClient
                .get()
                .uri(ChartResource.CHARTS + ChartResource.CHART_CATEGORY + ChartResource.CHART_GROUP_BY_TYPE, ChartCategory.EXPENSES, ExpenseChartGroupBy.EXPENSE_CATEGORY)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testGetFilteredChartNoFiltersBadRequest() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ChartResource.CHARTS + ChartResource.FILTER)
                .bodyValue(FilterChartDto.builder()
                        .chartCategory(ChartCategory.EXPENSES.name())
                        .chartGroupByType(ExpenseChartGroupBy.EXPENSE_CATEGORY.name())
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetFilteredChartChartCategoryBadRequest() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ChartResource.CHARTS + ChartResource.FILTER)
                .bodyValue(FilterChartDto.builder()
                        .chartCategory("INVALID")
                        .chartGroupByType(ExpenseChartGroupBy.EXPENSE_CATEGORY.name())
                        .filterDtos(List.of(FilterDto.builder().build()))
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetFilteredChartChartGroupByTypeBadRequest() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ChartResource.CHARTS + ChartResource.FILTER)
                .bodyValue(FilterChartDto.builder()
                        .chartCategory(ChartCategory.EXPENSES.name())
                        .chartGroupByType("INVALID")
                        .filterDtos(List.of(FilterDto.builder().build()))
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetFilteredChartUnauthorized() {
        webTestClient
                .post()
                .uri(ChartResource.CHARTS + ChartResource.FILTER)
                .bodyValue(FilterChartDto.builder()
                        .chartCategory(ChartCategory.EXPENSES.name())
                        .chartGroupByType(ExpenseChartGroupBy.EXPENSE_CATEGORY.name())
                        .filterDtos(List.of(FilterDto.builder().build()))
                        .build())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testGetFilteredChartChartCategoryNullBadRequest() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ChartResource.CHARTS + ChartResource.FILTER)
                .bodyValue(FilterChartDto.builder()
                        .chartCategory(null)
                        .chartGroupByType(ExpenseChartGroupBy.EXPENSE_CATEGORY.name())
                        .filterDtos(List.of(FilterDto.builder().build()))
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetFilteredChartFilterDtoBadRequest() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ChartResource.CHARTS + ChartResource.FILTER)
                .bodyValue(FilterChartDto.builder()
                        .chartCategory(ChartCategory.EXPENSES.name())
                        .chartGroupByType(ExpenseChartGroupBy.EXPENSE_CATEGORY.name())
                        .filterDtos(List.of(FilterDto.builder().filterComparison(null).filterField(FilterField.AMOUNT.name()).filterValue("1.22").build()))
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetFilteredChartSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ChartResource.CHARTS + ChartResource.FILTER)
                .bodyValue(FilterChartDto.builder()
                        .chartCategory(ChartCategory.EXPENSES.name())
                        .chartGroupByType(ExpenseChartGroupBy.EXPENSE_CATEGORY.name())
                        .filterDtos(List.of(FilterDto.builder().filterComparison(FilterComparisons.EQUAL.name())
                                .filterField(FilterField.AMOUNT.name())
                                .filterValue("1")
                                .build()))
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Chart.class)
                .value(chart -> {
                    assertNotNull(chart);
                    assertNotNull(chart.getData());
                    assertFalse(chart.getData().isEmpty());
                });
    }

    @Test
    void testGetFilteredChartIncomesSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ChartResource.CHARTS + ChartResource.FILTER)
                .bodyValue(FilterChartDto.builder()
                        .chartCategory(ChartCategory.INCOMES.name())
                        .chartGroupByType(IncomeChartGroupBy.INCOME_DATE.name())
                        .filterDtos(List.of(FilterDto.builder().filterComparison(FilterComparisons.EQUAL.name())
                                .filterField(FilterField.AMOUNT.name())
                                .filterValue("1")
                                .build()))
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Chart.class)
                .value(chart -> {
                    assertNotNull(chart);
                    assertNotNull(chart.getData());
                    assertFalse(chart.getData().isEmpty());
                });
    }

    @Test
    void testGetFilteredSeriesChartSuccess() {
        restClientTestService
                .loginDefault(webTestClient)
                .post()
                .uri(ChartResource.CHARTS + ChartResource.FILTER)
                .bodyValue(FilterChartDto.builder()
                        .chartCategory(ChartCategory.EXPENSE_INCOME_SERIES.name())
                        .filterDtos(List.of(
                                FilterDto.builder().filterComparison(FilterComparisons.EQUAL.name())
                                    .filterField(FilterField.AMOUNT.name())
                                    .filterValue("1")
                                    .build(),
                                FilterDto.builder().filterComparison(FilterComparisons.EQUAL.name())
                                    .filterField(FilterField.INCOME_DATE.name())
                                    .filterValue(TODAY_STRING)
                                    .build(),
                                FilterDto.builder().filterComparison(FilterComparisons.EQUAL.name())
                                    .filterField(FilterField.EXPENSE_DATE.name())
                                    .filterValue(TODAY_STRING)
                                    .build(),
                                FilterDto.builder().filterComparison(FilterComparisons.EQUAL.name())
                                    .filterField(FilterField.EXPENSE_CATEGORY.name())
                                    .filterValue("userNameUserExpenseCategory")
                                    .build()
                        ))
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Chart.class)
                .value(chart -> {
                    assertNotNull(chart);
                    assertNotNull(chart.getSeries());
                    assertFalse(chart.getSeries().isEmpty());
                    System.out.println(chart.getSeries().get(0).getName());
                    assertTrue(chart.getSeries().stream().anyMatch(sc -> sc.getName().contains(RETURNED_TODAY_STRING)));
                    assertEquals(2, chart.getSeries().stream().filter(sc -> sc.getName().contains(RETURNED_TODAY_STRING))
                            .mapToLong(sc -> sc.getSeries().size())
                            .sum());
                });
    }
}
