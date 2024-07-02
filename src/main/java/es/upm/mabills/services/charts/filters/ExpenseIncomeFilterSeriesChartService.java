package es.upm.mabills.services.charts.filters;

import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.ChartData;
import es.upm.mabills.model.SeriesChartData;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.model.filters.Filter;
import es.upm.mabills.model.filters.FilterField;
import es.upm.mabills.persistence.FilterPersistence;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.persistence.entities.IncomeEntity;
import io.vavr.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Qualifier("expenseIncomeFilterSeriesChartService")
@Service
public class ExpenseIncomeFilterSeriesChartService implements FilterChartService {
    private static final String INCOME_SERIES_NAME = "Income";
    private static final String EXPENSE_SERIES_NAME = "Expense";
    private static final Logger LOGGER = LogManager.getLogger(ExpenseIncomeFilterSeriesChartService.class);
    private final FilterPersistence filterPersistence;
    protected final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Autowired
    public ExpenseIncomeFilterSeriesChartService(FilterPersistence filterPersistence) {
        this.filterPersistence = filterPersistence;
    }

    @Override
    public Chart getChart(UserPrincipal userPrincipal, String groupBy, List<Filter> filters) {
        return Try.of(() -> Chart.builder()
                    .series(buildSeriesChartData(userPrincipal, filters))
                    .build())
                .getOrElseThrow(e ->{
                    LOGGER.error(e);
                    return new MaBillsServiceException("Could not retrieve chart data.");
                });
    }

    private List<SeriesChartData> buildSeriesChartData(UserPrincipal userPrincipal, List<Filter> filters) {
        return Stream.concat(getFilteredExpenses(userPrincipal, filters), getFilteredIncomes(userPrincipal, filters))
                .collect(Collectors.groupingBy(SeriesChartData::getName))
                .entrySet()
                .stream()
                .map(entry -> SeriesChartData.builder()
                        .name(entry.getKey())
                        .series(entry.getValue().stream()
                                .flatMap(sc -> sc.getSeries()
                                            .stream())
                                        .toList())
                        .build())
                .sorted(Comparator.comparing(SeriesChartData::getName))
                .toList();
    }


    private Stream<SeriesChartData> getFilteredExpenses(UserPrincipal userPrincipal, List<Filter> filters) {
        List<Filter> expensesFilters = filters.stream()
                .filter(filter -> !filter.getFilterField().equals(FilterField.INCOME_DATE))
                .toList();
        return this.filterPersistence.applyFilters(expensesFilters, ExpenseEntity.class, userPrincipal)
                .stream()
                .collect(Collectors.groupingBy(ex -> Objects.isNull(ex.getExpenseDate()) ? "" :
                                simpleDateFormat.format(ex.getExpenseDate()),
                        Collectors.reducing(BigDecimal.ZERO, ExpenseEntity::getAmount, BigDecimal::add)))
                .entrySet()
                .stream()
                .map(entry -> buildSeriesChartDataFromEntry(entry, EXPENSE_SERIES_NAME));
    }

    private Stream<SeriesChartData> getFilteredIncomes(UserPrincipal userPrincipal, List<Filter> filters) {
        List<Filter> incomeFilters = filters.stream()
                .filter(filter -> !filter.getFilterField().equals(FilterField.EXPENSE_CATEGORY)
                        && !filter.getFilterField().equals(FilterField.EXPENSE_DATE)
                        && !filter.getFilterField().equals(FilterField.FORM_OF_PAYMENT))
                .toList();
        return this.filterPersistence.applyFilters(incomeFilters, IncomeEntity.class, userPrincipal)
                .stream()
                .collect(Collectors.groupingBy(in -> Objects.isNull(in.getIncomeDate()) ? "" :
                                simpleDateFormat.format(in.getIncomeDate()),
                        Collectors.reducing(BigDecimal.ZERO, IncomeEntity::getAmount, BigDecimal::add)))
                .entrySet()
                .stream()
                .map(entry -> buildSeriesChartDataFromEntry(entry, INCOME_SERIES_NAME));
    }

    private SeriesChartData buildSeriesChartDataFromEntry(Map.Entry<String, BigDecimal> entry, String serieName) {
        return SeriesChartData.builder()
                .name(entry.getKey())
                .series(
                    List.of(ChartData.builder()
                        .name(serieName)
                        .value(entry.getValue())
                        .build())
                )
                .build();
    }


}
