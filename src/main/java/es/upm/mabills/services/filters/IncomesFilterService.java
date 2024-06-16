package es.upm.mabills.services.filters;

import es.upm.mabills.mappers.IncomeMapper;
import es.upm.mabills.model.Income;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.model.filters.Filter;
import es.upm.mabills.persistence.FilterPersistence;
import es.upm.mabills.persistence.entities.IncomeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Qualifier("incomesFilterService")
@Service
public class IncomesFilterService implements FilterService<Income> {
    private final FilterPersistence filterPersistence;
    private final IncomeMapper incomeMapper;

    @Autowired
    public IncomesFilterService(FilterPersistence filterPersistence, IncomeMapper incomeMapper) {
        this.filterPersistence = filterPersistence;
        this.incomeMapper = incomeMapper;
    }

    public List<Income> applyFilters(List<Filter> filters, UserPrincipal userPrincipal) {
        return filterPersistence.applyFilters(filters, IncomeEntity.class, userPrincipal)
                .stream()
                .map(incomeMapper::toIncome)
                .toList();
    }
}
