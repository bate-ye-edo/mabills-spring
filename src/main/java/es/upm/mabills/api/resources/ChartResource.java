package es.upm.mabills.api.resources;

import es.upm.mabills.api.Rest;
import es.upm.mabills.model.Chart;
import es.upm.mabills.services.charts.ChartCategory;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.services.charts.ChartServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Rest
@RequestMapping(ChartResource.CHARTS)
public class ChartResource {
    public static final String CHARTS = "/charts";
    public static final String CHART_CATEGORY = "/{chart-category}";
    public static final String CHART_GROUP_BY_TYPE = "/{group-by}";


    private final ChartServiceFactory chartServiceFactory;

    @Autowired
    public ChartResource(ChartServiceFactory chartServiceFactory) {
        this.chartServiceFactory = chartServiceFactory;
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
}
