package es.upm.mabills.services.charts;

import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.mappers.ChartDataMapper;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.ChartData;
import es.upm.mabills.model.SeriesChartData;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.ExpensePersistence;
import es.upm.mabills.persistence.IncomePersistence;
import es.upm.mabills.persistence.chart_data_dtos.DateChartData;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
@Qualifier("expenseIncomeSeriesChartService")
public class ExpenseIncomeSeriesChartService implements ChartService {
    private static final String INCOME_SERIE_NAME = "Income";
    private static final String EXPENSE_SERIE_NAME = "Expense";
    private final IncomePersistence incomePersistence;
    private final ExpensePersistence expensePersistence;
    private final ChartDataMapper chartDataMapper;

    @Autowired
    public ExpenseIncomeSeriesChartService(IncomePersistence incomePersistence, ExpensePersistence expensePersistence,
                                           ChartDataMapper chartDataMapper) {
        this.incomePersistence = incomePersistence;
        this.expensePersistence = expensePersistence;
        this.chartDataMapper = chartDataMapper;
    }

    @Override
    public Chart getChart(UserPrincipal userPrincipal, String groupBy) {
        return Try.of(()->buildSeriesChartFromCharts(incomePersistence.getIncomesGroupByDateChartData(userPrincipal),
                    expensePersistence.getExpensesGroupByDateChartData(userPrincipal)))
                .getOrElseThrow(MaBillsServiceException::new);
    }

    private Chart buildSeriesChartFromCharts(List<DateChartData> incomeChart, List<DateChartData> expenseChart) {
        return Chart.builder()
                .series(buildSeries(incomeChart, expenseChart))
                .build();
    }

    private List<SeriesChartData> buildSeries(List<DateChartData> incomeChart, List<DateChartData> expenseChart) {
        return Stream.concat(mapToSeriesChartData(incomeChart.stream(), INCOME_SERIE_NAME), mapToSeriesChartData(expenseChart.stream(), EXPENSE_SERIE_NAME))
                .collect(Collectors.groupingBy(SeriesChartData::getName))
                .entrySet()
                .stream()
                .map(entry -> SeriesChartData.builder()
                        .name(entry.getKey())
                        .series(entry.getValue()
                                .stream()
                                .flatMap(sc -> sc.getSeries().stream())
                                .collect(Collectors.groupingBy(ChartData::getName,
                                        Collectors.reducing(BigDecimal.ZERO, ChartData::getValue, BigDecimal::add)))
                                .entrySet()
                                .stream()
                                .map(e -> ChartData.builder()
                                        .name(e.getKey())
                                        .value(e.getValue())
                                        .build())
                                .toList())
                        .build())
                .sorted(Comparator.comparing(SeriesChartData::getName))
                .toList();
    }

    private Stream<SeriesChartData> mapToSeriesChartData(Stream<DateChartData> dateChartData, String serieName) {
        return dateChartData.map(
                    dcd -> {
                        ChartData chartData = chartDataMapper.toChartData(dcd);
                        return SeriesChartData.builder()
                                .name(chartData.getName())
                                .series(List.of(
                                        ChartData.builder()
                                                .name(serieName)
                                                .value(chartData.getValue())
                                                .build()
                                ))
                                .build();
                    }
                );
    }
}
