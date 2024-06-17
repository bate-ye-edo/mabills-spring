package es.upm.mabills.services.charts.filters;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.SeriesChartData;
import es.upm.mabills.model.filters.Filter;
import es.upm.mabills.persistence.FilterPersistence;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.persistence.entities.IncomeEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@UnitTestConfig
class ExpenseIncomeFilterSeriesChartServiceTest {
    private static final Timestamp TODAY = new Timestamp(System.currentTimeMillis());
    private static final Timestamp YESTERDAY = new Timestamp(System.currentTimeMillis() - 24 * 60 * 60 * 1000);

    @Autowired
    private ExpenseIncomeFilterSeriesChartService expenseIncomeFilterSeriesChartService;

    @MockBean
    private FilterPersistence filterPersistence;

    @Test
    void testGetSeriesChartDataSuccess() {
        when(filterPersistence.applyFilters(anyList(), eq(ExpenseEntity.class), any())).thenReturn(List.of(
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).build(),
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).build(),
                ExpenseEntity.builder().amount(BigDecimal.TEN).expenseDate(YESTERDAY).build()
        ));
        when(filterPersistence.applyFilters(anyList(), eq(IncomeEntity.class), any())).thenReturn(List.of(
                IncomeEntity.builder().amount(BigDecimal.ONE).incomeDate(TODAY).build(),
                IncomeEntity.builder().amount(BigDecimal.ONE).incomeDate(TODAY).build(),
                IncomeEntity.builder().amount(BigDecimal.TEN).incomeDate(YESTERDAY).build()
        ));
        Chart chart = expenseIncomeFilterSeriesChartService.getChart(null, null, List.of());
        assertNotNull(chart);
        assertNotNull(chart.getSeries());
        assertFalse(chart.getSeries().isEmpty());
        assertNotNull(chart.getSeries().get(0));
        assertNotNull(chart.getSeries().get(0).getSeries());
        assertFalse(chart.getSeries().get(0).getSeries().isEmpty());
        assertNotNull(chart.getSeries().get(1));
        assertNotNull(chart.getSeries().get(1).getSeries());
        assertFalse(chart.getSeries().get(1).getSeries().isEmpty());
        List<SeriesChartData> series = chart.getSeries();
        assertTrue(series.stream().anyMatch(sc -> sc.getName().equals(TODAY.toString()) && sc.getSeries().size() == 2));
        assertTrue(series.stream().anyMatch(sc -> sc.getName().equals(YESTERDAY.toString()) && sc.getSeries().size() == 2));
    }

    @Test
    void testGetSeriesChartDataEmpty() {
        when(filterPersistence.applyFilters(anyList(), eq(ExpenseEntity.class), any())).thenReturn(List.of());
        when(filterPersistence.applyFilters(anyList(), eq(IncomeEntity.class), any())).thenReturn(List.of());
        Chart chart = expenseIncomeFilterSeriesChartService.getChart(null, null, List.of());
        assertNotNull(chart);
        assertNotNull(chart.getSeries());
        assertTrue(chart.getSeries().isEmpty());
    }

    @Test
    void testGetSeriesChartDataThrowsException() {
        when(filterPersistence.applyFilters(anyList(), eq(ExpenseEntity.class), any())).thenThrow(new RuntimeException());
        when(filterPersistence.applyFilters(anyList(), eq(IncomeEntity.class), any())).thenThrow(new RuntimeException());
        List<Filter> filters = List.of();
        assertThrows(MaBillsServiceException.class, () -> expenseIncomeFilterSeriesChartService.getChart(null, null, filters));
    }

    @Test
    void testGetSeriesChartDataSomeDateEmpty() {
        when(filterPersistence.applyFilters(anyList(), eq(ExpenseEntity.class), any())).thenReturn(List.of(
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).build(),
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).build(),
                ExpenseEntity.builder().amount(BigDecimal.TEN).expenseDate(null).build()
        ));
        when(filterPersistence.applyFilters(anyList(), eq(IncomeEntity.class), any())).thenReturn(List.of());
        Chart chart = expenseIncomeFilterSeriesChartService.getChart(null, null, List.of());
        assertNotNull(chart);
        assertNotNull(chart.getSeries());
        assertFalse(chart.getSeries().isEmpty());
        assertNotNull(chart.getSeries().get(0));
        assertNotNull(chart.getSeries().get(0).getSeries());
        assertFalse(chart.getSeries().get(0).getSeries().isEmpty());
        assertNotNull(chart.getSeries().get(1));
        assertNotNull(chart.getSeries().get(1).getSeries());
        assertFalse(chart.getSeries().get(1).getSeries().isEmpty());
        List<SeriesChartData> series = chart.getSeries();
        assertTrue(series.stream().anyMatch(sc -> sc.getName().equals(TODAY.toString()) && sc.getSeries().size() == 1));
        assertTrue(series.stream().anyMatch(sc -> sc.getName().isEmpty() && sc.getSeries().size() == 1));
    }

    @Test
    void testGetSeriesChartDataSomeIncomeDateEmpty() {
        when(filterPersistence.applyFilters(anyList(), eq(ExpenseEntity.class), any())).thenReturn(List.of(
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).build(),
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).build(),
                ExpenseEntity.builder().amount(BigDecimal.TEN).expenseDate(null).build()
        ));
        when(filterPersistence.applyFilters(anyList(), eq(IncomeEntity.class), any())).thenReturn(List.of(
                IncomeEntity.builder().amount(BigDecimal.ONE).incomeDate(TODAY).build(),
                IncomeEntity.builder().amount(BigDecimal.ONE).incomeDate(TODAY).build(),
                IncomeEntity.builder().amount(BigDecimal.TEN).incomeDate(null).build()
        ));
        Chart chart = expenseIncomeFilterSeriesChartService.getChart(null, null, List.of());
        assertNotNull(chart);
        assertNotNull(chart.getSeries());
        assertFalse(chart.getSeries().isEmpty());
        assertNotNull(chart.getSeries().get(0));
        assertNotNull(chart.getSeries().get(0).getSeries());
        assertFalse(chart.getSeries().get(0).getSeries().isEmpty());
        assertNotNull(chart.getSeries().get(1));
        assertNotNull(chart.getSeries().get(1).getSeries());
        assertFalse(chart.getSeries().get(1).getSeries().isEmpty());
        List<SeriesChartData> series = chart.getSeries();
        assertTrue(series.stream().anyMatch(sc -> sc.getName().equals(TODAY.toString()) && sc.getSeries().size() == 2));
        assertTrue(series.stream().anyMatch(sc -> sc.getName().isEmpty() && sc.getSeries().size() == 2));
    }
}
