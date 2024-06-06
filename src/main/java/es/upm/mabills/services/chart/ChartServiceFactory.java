package es.upm.mabills.services.chart;

import es.upm.mabills.model.ChartDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChartServiceFactory {
    private final Map<ChartDataType, ChartService> chartServicesMap;

    @Autowired
    public ChartServiceFactory(@Qualifier("expensesChartService") ChartService expenseChartService,
                               @Qualifier("incomesChartService") ChartService incomeChartService) {
        this.chartServicesMap = Map.of(
                ChartDataType.EXPENSES, expenseChartService,
                ChartDataType.INCOMES, incomeChartService
        );
    }

    public ChartService getChartService(ChartDataType chartDataType) {
        return chartServicesMap.get(chartDataType);
    }
}
