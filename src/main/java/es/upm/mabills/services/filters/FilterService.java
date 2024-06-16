package es.upm.mabills.services.filters;

import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.model.filters.Filter;

import java.util.List;

public interface FilterService<T>{
    List<T> applyFilters(List<Filter> filters, UserPrincipal userPrincipal);
}
