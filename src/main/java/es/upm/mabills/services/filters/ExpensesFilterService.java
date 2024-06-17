package es.upm.mabills.services.filters;

import es.upm.mabills.mappers.ExpenseMapper;
import es.upm.mabills.model.Expense;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.model.filters.Filter;
import es.upm.mabills.persistence.FilterPersistence;
import es.upm.mabills.persistence.entities.ExpenseEntity;
import es.upm.mabills.services.exception_mappers.FilterExceptionMapper;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Qualifier("expensesFilterService")
@Service
public class ExpensesFilterService implements FilterService<Expense> {
    private final FilterPersistence filterPersistence;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpensesFilterService(FilterPersistence filterPersistence, ExpenseMapper expenseMapper) {
        this.filterPersistence = filterPersistence;
        this.expenseMapper = expenseMapper;
    }

    @Override
    public List<Expense> applyFilters(List<Filter> filters, UserPrincipal userPrincipal) {
        return Try.of(() -> filterPersistence.applyFilters(filters, ExpenseEntity.class, userPrincipal))
                .map(expenseEntities -> expenseEntities.stream()
                        .map(expenseMapper::toExpense)
                        .toList())
                .getOrElseThrow(FilterExceptionMapper::map);
    }
}
