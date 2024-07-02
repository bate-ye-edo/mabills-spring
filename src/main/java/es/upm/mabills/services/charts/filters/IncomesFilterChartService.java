package es.upm.mabills.services.charts.filters;

import es.upm.mabills.mappers.IncomeMapper;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.ChartData;
import es.upm.mabills.model.Income;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.model.filters.Filter;
import es.upm.mabills.model.filters.FilterField;
import es.upm.mabills.persistence.FilterPersistence;
import es.upm.mabills.persistence.entities.IncomeEntity;
import es.upm.mabills.services.charts.IncomeChartGroupBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

@Qualifier("incomesFilterChartService")
@Service
public class IncomesFilterChartService extends AbstractChartFilterService<Income> {
    private final FilterPersistence filterPersistence;
    private final IncomeMapper incomeMapper;

    @Autowired
    public IncomesFilterChartService(FilterPersistence filterPersistence, IncomeMapper expenseMapper) {
        this.filterPersistence = filterPersistence;
        this.incomeMapper = expenseMapper;
    }

    @Override
    protected Chart buildFilteredChartByGroupByType(UserPrincipal userPrincipal, String groupBy, List<Filter> filters) {
        return Chart.builder()
                .data(buildChartDataList(userPrincipal, IncomeChartGroupBy.fromString(groupBy), filters))
                .build();
    }

    private List<ChartData> buildChartDataList(UserPrincipal userPrincipal, IncomeChartGroupBy expenseChartGroupBy, List<Filter> filters) {
        List<Filter> incomeFilters = filters.stream()
                .filter(filter -> !filter.getFilterField().equals(FilterField.EXPENSE_CATEGORY)
                        && !filter.getFilterField().equals(FilterField.EXPENSE_DATE)
                        && !filter.getFilterField().equals(FilterField.FORM_OF_PAYMENT))
                .toList();
        return filterPersistence.applyFilters(incomeFilters, IncomeEntity.class, userPrincipal).stream()
                .map(incomeMapper::toIncome)
                .collect(getCollectorByGroupByType(expenseChartGroupBy))
                .entrySet()
                .stream()
                .map(entry -> buildChartData(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ChartData::getName))
                .toList();
    }

    private Collector<Income, ?, Map<String, BigDecimal>> getCollectorByGroupByType(IncomeChartGroupBy expenseChartGroupBy) {
        return switch (expenseChartGroupBy) {
            case INCOME_DATE -> getCollector(income -> emptyStringIfNull(in->simpleDateFormat.format(in.getIncomeDate()), income),
                    Income::getAmount);
            case INCOME_CREDIT_CARD -> getCollector(income -> emptyStringIfNull(in -> in.getCreditCard().getCreditCardNumber(), income),
                    Income::getAmount);
            case INCOME_BANK_ACCOUNT -> getCollector(income -> emptyStringIfNull(in -> in.getBankAccount().getIban(), income),
                    Income::getAmount);
        };
    }
}
