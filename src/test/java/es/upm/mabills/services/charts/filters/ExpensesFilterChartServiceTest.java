package es.upm.mabills.services.charts.filters;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.FormOfPayment;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.model.filters.Filter;
import es.upm.mabills.persistence.FilterPersistence;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entities.ExpenseCategoryEntity;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.services.charts.ExpenseChartGroupBy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
class ExpensesFilterChartServiceTest {
    private static final Timestamp TODAY = new Timestamp(System.currentTimeMillis());
    private static final Timestamp YESTERDAY = new Timestamp(System.currentTimeMillis() - 24 * 60 * 60 * 1000);

    @Autowired
    private ExpensesFilterChartService expensesFilterChartService;

    @MockBean
    private FilterPersistence filterPersistence;

    private final UserPrincipal userPrincipal = UserPrincipal.builder().build();

    @Test
    void testFilterExpenseChartByAmountSuccess() {
        when(filterPersistence.applyFilters(anyList(), any(), any())).thenReturn(List.of(
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).build(),
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).build(),
                ExpenseEntity.builder().amount(BigDecimal.TEN).expenseDate(YESTERDAY).build()
        ));
        Chart chart = expensesFilterChartService.getChart(userPrincipal, ExpenseChartGroupBy.EXPENSE_DATE.name(), List.of());
        assertNotNull(chart);
        assertNull(chart.getSeries());
        assertNotNull(chart.getData());
        assertEquals(2, chart.getData().size());
        assertTrue(chart.getData().stream().anyMatch(chartData -> chartData.getName().equals(TODAY.toString())
                && chartData.getValue().equals(BigDecimal.valueOf(2))));
        assertTrue(chart.getData().stream().anyMatch(chartData -> chartData.getName().equals(YESTERDAY.toString())
                && chartData.getValue().equals(BigDecimal.TEN)));
    }

    @Test
    void testFilterExpenseChartThrowsException() {
        when(filterPersistence.applyFilters(anyList(), any(), any())).thenThrow(new RuntimeException());
        List<Filter> filters = List.of();
        String groupBy = ExpenseChartGroupBy.EXPENSE_DATE.name();
        assertThrows(MaBillsServiceException.class, () -> expensesFilterChartService.getChart(userPrincipal, groupBy, filters));
    }

    @Test
    void testFilterExpenseChartByCategorySuccess() {
        ExpenseCategoryEntity expenseCategory = ExpenseCategoryEntity.builder().name("category1").build();
        ExpenseCategoryEntity expenseCategory2 = ExpenseCategoryEntity.builder().name("category2").build();
        when(filterPersistence.applyFilters(anyList(), any(), any())).thenReturn(List.of(
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).expenseCategory(expenseCategory).build(),
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).expenseCategory(expenseCategory).build(),
                ExpenseEntity.builder().amount(BigDecimal.TEN).expenseDate(YESTERDAY).expenseCategory(expenseCategory2).build()
        ));
        Chart chart = expensesFilterChartService.getChart(userPrincipal, ExpenseChartGroupBy.EXPENSE_CATEGORY.name(), List.of());
        assertNotNull(chart);
        assertNull(chart.getSeries());
        assertNotNull(chart.getData());
        assertEquals(2, chart.getData().size());
        assertTrue(chart.getData().stream().anyMatch(chartData -> chartData.getName().equals(expenseCategory.getName())
                && chartData.getValue().equals(BigDecimal.valueOf(2))));
        assertTrue(chart.getData().stream().anyMatch(chartData -> chartData.getName().equals(expenseCategory2.getName())
                && chartData.getValue().equals(BigDecimal.TEN)));
    }

    @Test
    void testFilterExpenseChartByCreditCardSuccess() {
        CreditCardEntity creditCard = CreditCardEntity.builder().creditCardNumber("creditCard1").build();
        CreditCardEntity creditCard2 = CreditCardEntity.builder().creditCardNumber("creditCard2").build();
        when(filterPersistence.applyFilters(anyList(), any(), any())).thenReturn(List.of(
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).creditCard(creditCard).build(),
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).creditCard(creditCard).build(),
                ExpenseEntity.builder().amount(BigDecimal.TEN).expenseDate(YESTERDAY).creditCard(creditCard2).build()
        ));
        Chart chart = expensesFilterChartService.getChart(userPrincipal, ExpenseChartGroupBy.EXPENSE_CREDIT_CARD.name(), List.of());
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
    void testFilterExpenseChartByBankAccountSuccess() {
        BankAccountEntity bankAccount = BankAccountEntity.builder().iban("iban1").build();
        BankAccountEntity bankAccount2 = BankAccountEntity.builder().iban("iban2").build();
        when(filterPersistence.applyFilters(anyList(), any(), any())).thenReturn(List.of(
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).bankAccount(bankAccount).build(),
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).bankAccount(bankAccount).build(),
                ExpenseEntity.builder().amount(BigDecimal.TEN).expenseDate(YESTERDAY).bankAccount(bankAccount2).build()
        ));
        Chart chart = expensesFilterChartService.getChart(userPrincipal, ExpenseChartGroupBy.EXPENSE_BANK_ACCOUNT.name(), List.of());
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
    void testFilterExpenseChartByFormOfPaymentSuccess() {
        when(filterPersistence.applyFilters(anyList(), any(), any())).thenReturn(List.of(
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).formOfPayment(FormOfPayment.CASH.name()).build(),
                ExpenseEntity.builder().amount(BigDecimal.ONE).expenseDate(TODAY).formOfPayment(FormOfPayment.CASH.name()).build(),
                ExpenseEntity.builder().amount(BigDecimal.TEN).expenseDate(YESTERDAY).formOfPayment(FormOfPayment.CARD.name()).build()
        ));
        Chart chart = expensesFilterChartService.getChart(userPrincipal, ExpenseChartGroupBy.EXPENSE_FORM_OF_PAYMENT.name(), List.of());
        assertNotNull(chart);
        assertNull(chart.getSeries());
        assertNotNull(chart.getData());
        assertEquals(2, chart.getData().size());
        assertTrue(chart.getData().stream().anyMatch(chartData -> chartData.getName().equals(FormOfPayment.CASH.name())
                && chartData.getValue().equals(BigDecimal.valueOf(2))));
        assertTrue(chart.getData().stream().anyMatch(chartData -> chartData.getName().equals(FormOfPayment.CARD.name())
                && chartData.getValue().equals(BigDecimal.TEN)));
    }
}
