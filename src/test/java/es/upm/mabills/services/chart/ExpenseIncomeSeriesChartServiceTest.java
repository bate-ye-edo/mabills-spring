package es.upm.mabills.services.chart;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.ExpensePersistence;
import es.upm.mabills.persistence.IncomePersistence;
import es.upm.mabills.persistence.chart_data_dtos.DateChartData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static es.upm.mabills.TestStringUtils.assertNotBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@UnitTestConfig
class ExpenseIncomeSeriesChartServiceTest {
    private static final DateChartData EXPENSE_CHART_DATA = DateChartData.builder()
            .date(new java.sql.Timestamp(0))
            .value(java.math.BigDecimal.ONE)
            .build();
    private static final DateChartData INCOME_CHART_DATA = DateChartData.builder()
            .date(new java.sql.Timestamp(0))
            .value(java.math.BigDecimal.ONE)
            .build();

    @Autowired
    private ExpenseIncomeSeriesChartService expenseIncomeSeriesChartService;

    @MockBean
    private ExpensePersistence expensePersistence;

    @MockBean
    private IncomePersistence incomePersistence;

    @BeforeEach
    void setUp() {
        when(incomePersistence.getIncomesGroupByDateChartData(any(UserPrincipal.class)))
            .thenReturn(List.of(INCOME_CHART_DATA));
        when(expensePersistence.getExpensesGroupByDateChartData(any(UserPrincipal.class)))
            .thenReturn(List.of(EXPENSE_CHART_DATA));
    }

    @Test
    void testGetChartSuccess() {
        Chart seriesChart = expenseIncomeSeriesChartService.getChart(new UserPrincipal(), null);
        assertNotNull(seriesChart);
        assertNotNull(seriesChart.getSeries());
        assertEquals(1, seriesChart.getSeries().size());
        assertNotBlank(seriesChart.getSeries().get(0).getName());
        assertEquals(BigDecimal.ONE, seriesChart.getSeries().get(0).getSeries().get(0).getValue());
        assertEquals(BigDecimal.ONE, seriesChart.getSeries().get(0).getSeries().get(1).getValue());
    }

    @Test
    void testGetChartEmpty() {
        when(incomePersistence.getIncomesGroupByDateChartData(any(UserPrincipal.class)))
            .thenReturn(List.of());
        when(expensePersistence.getExpensesGroupByDateChartData(any(UserPrincipal.class)))
            .thenReturn(List.of());
        Chart seriesChart = expenseIncomeSeriesChartService.getChart(new UserPrincipal(), null);
        assertNotNull(seriesChart);
        assertNotNull(seriesChart.getSeries());
        assertEquals(0, seriesChart.getSeries().size());
    }

    @Test
    void testGetChartExpensePersistenceThrowsException() {
        doThrow(RuntimeException.class).when(expensePersistence).getExpensesGroupByDateChartData(any(UserPrincipal.class));
        UserPrincipal userPrincipal = new UserPrincipal();
        assertThrows(MaBillsServiceException.class, () -> expenseIncomeSeriesChartService.getChart(userPrincipal, null));
    }

    @Test
    void testGetChartIncomePersistenceThrowsException() {
        doThrow(RuntimeException.class).when(incomePersistence).getIncomesGroupByDateChartData(any(UserPrincipal.class));
        UserPrincipal userPrincipal = new UserPrincipal();
        assertThrows(MaBillsServiceException.class, () -> expenseIncomeSeriesChartService.getChart(userPrincipal, null));
    }
}
