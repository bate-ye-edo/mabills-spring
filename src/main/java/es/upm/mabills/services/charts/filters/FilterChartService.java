package es.upm.mabills.services.charts.filters;

import es.upm.mabills.model.Chart;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.model.filters.Filter;

import java.util.List;

public interface FilterChartService {
    Chart getChart(UserPrincipal userPrincipal, String groupBy, List<Filter> filters);
}
