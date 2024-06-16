package es.upm.mabills.services.charts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChartServiceFactory {
    private final Map<ChartCategory, ChartService> chartServicesMap;

    @Autowired
    public ChartServiceFactory(@Qualifier("expensesChartService") ChartService expenseChartService,
                               @Qualifier("incomesChartService") ChartService incomeChartService,
                               @Qualifier("expenseIncomeSeriesChartService") ChartService expenseIncomeSeriesChartService) {
        this.chartServicesMap = Map.of(
                ChartCategory.EXPENSES, expenseChartService,
                ChartCategory.INCOMES, incomeChartService,
                ChartCategory.EXPENSE_INCOME_SERIES, expenseIncomeSeriesChartService
        );
    }

    public ChartService getChartService(ChartCategory chartCategory) {
        return chartServicesMap.get(chartCategory);
    }
}
