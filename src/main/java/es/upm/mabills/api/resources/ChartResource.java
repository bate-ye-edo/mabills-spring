package es.upm.mabills.api.resources;

import es.upm.mabills.api.Rest;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.ChartDataType;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.services.chart.ChartServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Rest
@RequestMapping(ChartResource.CHARTS)
public class ChartResource {
    public static final String CHARTS = "/charts";
    public static final String DATA_TYPE = "/{datatype}";

    private final ChartServiceFactory chartServiceFactory;

    @Autowired
    public ChartResource(ChartServiceFactory chartServiceFactory) {
        this.chartServiceFactory = chartServiceFactory;
    }

    @GetMapping(DATA_TYPE)
    public Chart getChart(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable("datatype") String dataType) {
        return this.chartServiceFactory.getChartService(ChartDataType.fromString(dataType))
                .getChart(userPrincipal);
    }
}
