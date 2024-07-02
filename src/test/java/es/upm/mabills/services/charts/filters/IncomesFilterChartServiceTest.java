package es.upm.mabills.services.charts.filters;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.model.filters.Filter;
import es.upm.mabills.model.filters.FilterField;
import es.upm.mabills.persistence.FilterPersistence;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.IncomeEntity;
import es.upm.mabills.services.charts.IncomeChartGroupBy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@UnitTestConfig
class IncomesFilterChartServiceTest {
    private static final Timestamp TODAY = new Timestamp(System.currentTimeMillis());
    private static final Timestamp YESTERDAY = new Timestamp(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");
    private static final String TODAY_STRING = DATE_FORMATTER.format(TODAY);
    private static final String YESTERDAY_STRING = DATE_FORMATTER.format(YESTERDAY);

    @Autowired
    private IncomesFilterChartService IncomesFilterChartService;

    @MockBean
    private FilterPersistence filterPersistence;

    private final UserPrincipal userPrincipal = UserPrincipal.builder().build();

    @Test
    void testFilterIncomeChartByAmountSuccess() {
        when(filterPersistence.applyFilters(anyList(), any(), any())).thenReturn(List.of(
                IncomeEntity.builder().amount(BigDecimal.ONE).incomeDate(TODAY).build(),
                IncomeEntity.builder().amount(BigDecimal.ONE).incomeDate(TODAY).build(),
                IncomeEntity.builder().amount(BigDecimal.TEN).incomeDate(YESTERDAY).build()
        ));
        Chart chart = IncomesFilterChartService.getChart(userPrincipal, IncomeChartGroupBy.INCOME_DATE.name(), List.of());
        assertNotNull(chart);
        assertNull(chart.getSeries());
        assertNotNull(chart.getData());
        assertEquals(2, chart.getData().size());
        assertTrue(chart.getData().stream().anyMatch(chartData -> chartData.getName().equals(TODAY_STRING)
                && chartData.getValue().equals(BigDecimal.valueOf(2))));
        assertTrue(chart.getData().stream().anyMatch(chartData -> chartData.getName().equals(YESTERDAY_STRING)
                && chartData.getValue().equals(BigDecimal.TEN)));
    }

    @Test
    void testFilterIncomeChartThrowsException() {
        when(filterPersistence.applyFilters(anyList(), any(), any())).thenThrow(new RuntimeException());
        List<Filter> filters = List.of();
        String groupBy = IncomeChartGroupBy.INCOME_DATE.name();
        assertThrows(MaBillsServiceException.class, () -> IncomesFilterChartService.getChart(userPrincipal, groupBy, filters));
    }

    @Test
    void testFilterIncomeChartByCreditCardSuccess() {
        CreditCardEntity creditCard = CreditCardEntity.builder().creditCardNumber("creditCard1").build();
        CreditCardEntity creditCard2 = CreditCardEntity.builder().creditCardNumber("creditCard2").build();
        when(filterPersistence.applyFilters(anyList(), any(), any())).thenReturn(List.of(
                IncomeEntity.builder().amount(BigDecimal.ONE).incomeDate(TODAY).creditCard(creditCard).build(),
                IncomeEntity.builder().amount(BigDecimal.ONE).incomeDate(TODAY).creditCard(creditCard).build(),
                IncomeEntity.builder().amount(BigDecimal.TEN).incomeDate(YESTERDAY).creditCard(creditCard2).build()
        ));
        Chart chart = IncomesFilterChartService.getChart(userPrincipal, IncomeChartGroupBy.INCOME_CREDIT_CARD.name(), List.of());
        assertNotNull(chart);
        assertNull(chart.getSeries());
        assertNotNull(chart.getData());
        assertEquals(2, chart.getData().size());
        assertTrue(chart.getData().stream().anyMatch(chartData -> chartData.getName().equals(creditCard.getCreditCardNumber())
                && chartData.getValue().equals(BigDecimal.valueOf(2))));
        assertTrue(chart.getData().stream().anyMatch(chartData -> chartData.getName().equals(creditCard2.getCreditCardNumber())
                && chartData.getValue().equals(BigDecimal.TEN)));
    }

    @Test
    void testFilterIncomeChartByBankAccountSuccess() {
        BankAccountEntity bankAccount = BankAccountEntity.builder().iban("iban1").build();
        BankAccountEntity bankAccount2 = BankAccountEntity.builder().iban("iban2").build();
        when(filterPersistence.applyFilters(anyList(), any(), any())).thenReturn(List.of(
                IncomeEntity.builder().amount(BigDecimal.ONE).incomeDate(TODAY).bankAccount(bankAccount).build(),
                IncomeEntity.builder().amount(BigDecimal.ONE).incomeDate(TODAY).bankAccount(bankAccount).build(),
                IncomeEntity.builder().amount(BigDecimal.TEN).incomeDate(YESTERDAY).bankAccount(bankAccount2).build()
        ));
        Chart chart = IncomesFilterChartService.getChart(userPrincipal, IncomeChartGroupBy.INCOME_BANK_ACCOUNT.name(), List.of());
        assertNotNull(chart);
        assertNull(chart.getSeries());
        assertNotNull(chart.getData());
        assertEquals(2, chart.getData().size());
        assertTrue(chart.getData().stream().anyMatch(chartData -> chartData.getName().equals(bankAccount.getIban())
                && chartData.getValue().equals(BigDecimal.valueOf(2))));
        assertTrue(chart.getData().stream().anyMatch(chartData -> chartData.getName().equals(bankAccount2.getIban())
                && chartData.getValue().equals(BigDecimal.TEN)));
    }

    @Test
    void testFilterIncomeChartByExpenseFiltersSuccess() {
        when(filterPersistence.applyFilters(anyList(), any(), any())).thenReturn(List.of(
                IncomeEntity.builder().amount(BigDecimal.ONE).incomeDate(TODAY).build(),
                IncomeEntity.builder().amount(BigDecimal.ONE).incomeDate(TODAY).build(),
                IncomeEntity.builder().amount(BigDecimal.TEN).incomeDate(YESTERDAY).build()
        ));
        Chart chart = IncomesFilterChartService.getChart(userPrincipal, IncomeChartGroupBy.INCOME_DATE.name(), List.of(
                Filter.builder().filterField(FilterField.EXPENSE_DATE).build(),
                Filter.builder().filterField(FilterField.EXPENSE_CATEGORY).build(),
                Filter.builder().filterField(FilterField.FORM_OF_PAYMENT).build()
        ));
        assertNotNull(chart);
        assertNull(chart.getSeries());
        assertNotNull(chart.getData());
        assertEquals(2, chart.getData().size());
        assertTrue(chart.getData().stream().anyMatch(chartData -> chartData.getName().equals(TODAY_STRING)
                && chartData.getValue().equals(BigDecimal.valueOf(2))));
        assertTrue(chart.getData().stream().anyMatch(chartData -> chartData.getName().equals(YESTERDAY_STRING)
                && chartData.getValue().equals(BigDecimal.TEN)));
    }
}

