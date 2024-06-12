package es.upm.mabills.services.chart;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.exceptions.InvalidRequestException;
import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.ChartData;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.ExpensePersistence;
import es.upm.mabills.persistence.chart_data_dtos.DateChartData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static es.upm.mabills.TestStringUtils.assertNotBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@UnitTestConfig
class ExpensesChartServiceTest {
    @Autowired
    private ExpensesChartService expensesChartService;

    @MockBean
    private ExpensePersistence expensePersistence;

    @Test
    void testGetChartSuccess() {
        when(expensePersistence.getExpensesGroupByDateChartData(any(UserPrincipal.class)))
            .thenReturn(List.of(new DateChartData(new Timestamp(0), BigDecimal.ONE)));
        Chart chart = expensesChartService.getChart(new UserPrincipal(), ExpenseChartGroupBy.EXPENSE_DATE.name());
        assertNotNull(chart);
        assertFalse(chart.getData().isEmpty());
        assertNotBlank(chart.getData().get(0).getName());
        assertEquals(BigDecimal.ONE, chart.getData().get(0).getValue());
    }

    @Test
    void testGetChartRuntimeException() {
        when(expensePersistence.getExpensesGroupByDateChartData(any(UserPrincipal.class)))
            .thenThrow(new RuntimeException());
        UserPrincipal userPrincipal = new UserPrincipal();
        assertThrows(MaBillsServiceException.class, () -> expensesChartService.getChart(userPrincipal, null));
    }

    @Test
    void testGetChartEmpty() {
        when(expensePersistence.getExpensesGroupByDateChartData(any(UserPrincipal.class)))
            .thenReturn(List.of());
        Chart chart = expensesChartService.getChart(new UserPrincipal(), null);
        assertNotNull(chart);
        assertTrue(chart.getData().isEmpty());
    }

    @Test
    void testGetChartDatabaseException() {
        when(expensePersistence.getExpensesGroupByDateChartData(any(UserPrincipal.class)))
            .thenThrow(new DataIntegrityViolationException(""));
        UserPrincipal userPrincipal = new UserPrincipal();
        assertThrows(MaBillsServiceException.class, () -> expensesChartService.getChart(userPrincipal, null));
    }

    @Test
    void testGetChartByGroupByType() {
        UserPrincipal userPrincipal = new UserPrincipal();
        when(expensePersistence.getExpensesGroupByCategoryChartData(userPrincipal))
            .thenReturn(List.of(new ChartData("", BigDecimal.ONE)));
        Chart chart = expensesChartService.getChart(userPrincipal, ExpenseChartGroupBy.EXPENSE_CATEGORY.name());
        assertNotNull(chart);
        assertFalse(chart.getData().isEmpty());
        assertEquals(BigDecimal.ONE, chart.getData().get(0).getValue());
    }

    @Test
    void testGetChartByGroupByTypeRuntimeException() {
        when(expensePersistence.getExpensesGroupByCategoryChartData(any(UserPrincipal.class)))
            .thenThrow(new RuntimeException());
        UserPrincipal userPrincipal = new UserPrincipal();
        String groupBy = ExpenseChartGroupBy.EXPENSE_CATEGORY.name();
        assertThrows(MaBillsServiceException.class, () -> expensesChartService.getChart(userPrincipal, groupBy));
    }

    @Test
    void testGetChartByGroupByTypeEmpty() {
        when(expensePersistence.getExpensesGroupByCategoryChartData(any(UserPrincipal.class)))
            .thenReturn(List.of());
        Chart chart = expensesChartService.getChart(new UserPrincipal(), ExpenseChartGroupBy.EXPENSE_CATEGORY.name());
        assertNotNull(chart);
        assertTrue(chart.getData().isEmpty());
    }

    @Test
    void testGetChartWrongGroupBy() {
        UserPrincipal userPrincipal = new UserPrincipal();
        when(expensePersistence.getExpensesGroupByDateChartData(userPrincipal))
            .thenReturn(List.of(new DateChartData(new Timestamp(0), BigDecimal.ONE)));
        assertThrows(InvalidRequestException.class, () -> expensesChartService.getChart(userPrincipal, "WRONG_GROUP_BY"));
    }
}
