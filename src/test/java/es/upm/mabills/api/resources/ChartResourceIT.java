package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.model.Chart;
import es.upm.mabills.services.TokenCacheService;
import es.upm.mabills.services.chart.ChartCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ApiTestConfig
class ChartResourceIT {

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
    void getExpensesChartSuccess() {
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
    void getIncomesChartSuccess() {
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
    void getChartInvalidDataType() {
        restClientTestService
                .loginDefault(webTestClient)
                .get()
                .uri(ChartResource.CHARTS + ChartResource.CHART_CATEGORY, "INVALID")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getChartUnauthorized() {
        webTestClient
                .get()
                .uri(ChartResource.CHARTS + ChartResource.CHART_CATEGORY, ChartCategory.EXPENSES)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getExpensesChartEmpty() {
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
    void getIncomesChartEmpty() {
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
}
