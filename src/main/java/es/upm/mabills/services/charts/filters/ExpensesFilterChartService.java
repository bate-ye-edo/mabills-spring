package es.upm.mabills.services.charts.filters;

import es.upm.mabills.mappers.ExpenseMapper;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.ChartData;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.model.filters.Filter;
import es.upm.mabills.persistence.FilterPersistence;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.services.charts.ExpenseChartGroupBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;


@Qualifier("expensesFilterChartService")
@Service
public class ExpensesFilterChartService extends AbstractChartFilterService<Expense> {
    private final FilterPersistence filterPersistence;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpensesFilterChartService(FilterPersistence filterPersistence, ExpenseMapper expenseMapper) {
        this.filterPersistence = filterPersistence;
        this.expenseMapper = expenseMapper;
    }

    @Override
    protected Chart buildFilteredChartByGroupByType(UserPrincipal userPrincipal, String groupBy, List<Filter> filters) {
        return Chart.builder()
                .data(buildChartDataList(userPrincipal, ExpenseChartGroupBy.fromString(groupBy), filters))
                .build();
    }

    private List<ChartData> buildChartDataList(UserPrincipal userPrincipal, ExpenseChartGroupBy expenseChartGroupBy, List<Filter> filters) {
        return filterPersistence.applyFilters(filters, ExpenseEntity.class, userPrincipal).stream()
                .map(expenseMapper::toExpense)
                .collect(getCollectorByGroupByType(expenseChartGroupBy))
                .entrySet()
                .stream()
                .map(entry -> buildChartData(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ChartData::getName))
                .toList();
    }

    protected Collector<Expense, ?, Map<String, BigDecimal>> getCollectorByGroupByType(ExpenseChartGroupBy expenseChartGroupBy) {
        return switch (expenseChartGroupBy) {
            case EXPENSE_DATE -> getCollector(expense -> emptyStringIfNull(ex -> ex.getExpenseDate().toString(), expense),
                    Expense::getAmount);
            case EXPENSE_CATEGORY -> getCollector(expense -> emptyStringIfNull(ex -> ex.getExpenseCategory().getName(), expense),
                    Expense::getAmount);
            case EXPENSE_CREDIT_CARD -> getCollector(expense -> emptyStringIfNull(ex -> ex.getCreditCard().getCreditCardNumber(), expense),
                    Expense::getAmount);
            case EXPENSE_BANK_ACCOUNT -> getCollector(expense -> emptyStringIfNull(ex -> ex.getBankAccount().getIban(), expense),
                    Expense::getAmount);
            case EXPENSE_FORM_OF_PAYMENT -> getCollector(expense -> emptyStringIfNull(ex -> ex.getFormOfPayment().name(), expense),
                    Expense::getAmount);
        };
    }
}
