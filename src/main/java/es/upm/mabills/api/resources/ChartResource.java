package es.upm.mabills.api.resources;

import es.upm.mabills.api.Rest;
import es.upm.mabills.api.dtos.FilterChartDto;
import es.upm.mabills.mappers.FilterMapper;
import es.upm.mabills.model.Chart;
import es.upm.mabills.services.charts.ChartCategory;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.services.charts.ChartServiceFactory;
import es.upm.mabills.services.charts.filters.FilterChartServiceFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Rest
@RequestMapping(ChartResource.CHARTS)
public class ChartResource {
    public static final String CHARTS = "/charts";
    public static final String CHART_CATEGORY = "/{chart-category}";
    public static final String CHART_GROUP_BY_TYPE = "/{group-by}";
    public static final String FILTER = "/filter";
    private final ChartServiceFactory chartServiceFactory;
    private final FilterChartServiceFactory filterChartServiceFactory;
    private final FilterMapper filterMapper;

    @Autowired
    public ChartResource(ChartServiceFactory chartServiceFactory, FilterChartServiceFactory filterChartServiceFactory,
                         FilterMapper filterMapper) {
        this.chartServiceFactory = chartServiceFactory;
        this.filterChartServiceFactory = filterChartServiceFactory;
        this.filterMapper = filterMapper;
    }

    @GetMapping(CHART_CATEGORY)
    public Chart getChart(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable("chart-category") String chartCategory) {
        return this.chartServiceFactory.getChartService(ChartCategory.fromString(chartCategory))
                .getChart(userPrincipal, null);
    }

    @GetMapping(CHART_CATEGORY + CHART_GROUP_BY_TYPE)
    public Chart getChartGroupByType(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable("chart-category") String chartCategory, @PathVariable("group-by") String type) {
        return this.chartServiceFactory.getChartService(ChartCategory.fromString(chartCategory))
                .getChart(userPrincipal, type);
    }

    @PostMapping(FILTER)
    public Chart getFilteredChart(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid FilterChartDto filterChartDto) {
        return this.filterChartServiceFactory.getFilterChartService(ChartCategory.fromString(filterChartDto.chartCategory()))
                .getChart(userPrincipal, filterChartDto.chartGroupByType(),
                        filterChartDto
                                .filterDtos()
                                .stream()
                                .map(filterMapper::toFilter)
                                .toList());
    }
}
