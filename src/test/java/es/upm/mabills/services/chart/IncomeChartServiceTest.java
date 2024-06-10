package es.upm.mabills.services.chart;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.IncomePersistence;
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
class IncomeChartServiceTest {

    @Autowired
    private IncomesChartService incomesChartService;

    @MockBean
    private IncomePersistence incomePersistence;

    @Test
    void testGetChartSuccess() {
        when(incomePersistence.getIncomesGroupByDateChartData(any(UserPrincipal.class)))
                .thenReturn(List.of(new DateChartData(new Timestamp(0), BigDecimal.ONE)));
        Chart chart = incomesChartService.getChart(new UserPrincipal(), null);
        assertNotNull(chart);
        assertFalse(chart.getData().isEmpty());
        assertNotBlank(chart.getData().get(0).getName());
        assertEquals(BigDecimal.ONE, chart.getData().get(0).getValue());
    }

    @Test
    void testGetChartRuntimeException() {
        when(incomePersistence.getIncomesGroupByDateChartData(any(UserPrincipal.class)))
                .thenThrow(new RuntimeException());
        UserPrincipal userPrincipal = new UserPrincipal();
        assertThrows(MaBillsServiceException.class, () -> incomesChartService.getChart(userPrincipal, null));
    }

    @Test
    void testGetChartEmpty() {
        when(incomePersistence.getIncomesGroupByDateChartData(any(UserPrincipal.class)))
                .thenReturn(List.of());
        Chart chart = incomesChartService.getChart(new UserPrincipal(), null);
        assertNotNull(chart);
        assertTrue(chart.getData().isEmpty());
    }

    @Test
    void testGetChartDatabaseException() {
        when(incomePersistence.getIncomesGroupByDateChartData(any(UserPrincipal.class)))
                .thenThrow(new DataIntegrityViolationException(""));
        UserPrincipal userPrincipal = new UserPrincipal();
        assertThrows(MaBillsServiceException.class, () -> incomesChartService.getChart(userPrincipal, null));
    }
}
