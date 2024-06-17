package es.upm.mabills.services.charts.filters;

import es.upm.mabills.services.charts.ChartCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FilterChartServiceFactory {
    private final Map<ChartCategory, FilterChartService> filterChartServicesMap;

    @Autowired
    public FilterChartServiceFactory(@Qualifier("expensesFilterChartService") FilterChartService expenseChartService,
                                @Qualifier("incomesFilterChartService") FilterChartService incomeChartService,
                                @Qualifier("expenseIncomeFilterSeriesChartService") FilterChartService expenseIncomeSeriesChartService) {
        this.filterChartServicesMap = Map.of(
                ChartCategory.EXPENSES, expenseChartService,
                ChartCategory.INCOMES, incomeChartService,
                ChartCategory.EXPENSE_INCOME_SERIES, expenseIncomeSeriesChartService
        );
    }

    public FilterChartService getFilterChartService(ChartCategory chartCategory) {
        return filterChartServicesMap.get(chartCategory);
    }
}
