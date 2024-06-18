package es.upm.mabills.services.charts.filters;

import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.ChartData;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.model.filters.Filter;
import io.vavr.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public abstract class AbstractChartFilterService<T> implements FilterChartService {
    private static final Logger LOGGER = LogManager.getLogger(AbstractChartFilterService.class);
    protected final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public Chart getChart(UserPrincipal userPrincipal, String groupBy, List<Filter> filters) {
        return Try.of(() -> buildFilteredChartByGroupByType(userPrincipal, groupBy, filters))
                .getOrElseThrow(e -> {
                    LOGGER.error("Error getting chart", e);
                    return new MaBillsServiceException("Could not get chart.");
                });
    }
    protected abstract Chart buildFilteredChartByGroupByType(UserPrincipal userPrincipal, String groupBy, List<Filter> filters);

    protected  <R> Collector<T, ?, Map<R, BigDecimal>> getCollector(Function<T, R> groupByFunction, Function<T, BigDecimal> valueFunction) {
        return Collectors.groupingBy(groupByFunction, summingByAmount(valueFunction));
    }

    protected Collector<T, ?, BigDecimal> summingByAmount(Function<T, BigDecimal> valueFunction) {
        return Collectors.reducing(BigDecimal.ZERO, valueFunction, BigDecimal::add);
    }

    protected ChartData buildChartData(String name, BigDecimal value) {
        return ChartData.builder()
                .name(name)
                .value(value)
                .build();
    }

    protected String emptyStringIfNull(Function<T, String> getFieldFunction, T object) {
        try {
            return getFieldFunction.apply(object);
        } catch (NullPointerException e) {
            return "";
        }
    }

}
