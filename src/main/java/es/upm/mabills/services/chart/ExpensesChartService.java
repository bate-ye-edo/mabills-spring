package es.upm.mabills.services.chart;

import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.mappers.ChartDataMapper;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.ChartData;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.ExpensePersistence;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("expensesChartService")
public class ExpensesChartService implements ChartService {
    private final ExpensePersistence expensePersistence;
    private final ChartDataMapper chartDataMapper;

    @Autowired
    public ExpensesChartService(ExpensePersistence expensePersistence, ChartDataMapper chartDataMapper) {
        this.expensePersistence = expensePersistence;
        this.chartDataMapper = chartDataMapper;
    }

    @Override
    public Chart getChart(UserPrincipal userPrincipal) {
        return Chart.builder()
                .data(this.getExpensesGroupByDateChartData(userPrincipal))
                .build();
    }

    private List<ChartData> getExpensesGroupByDateChartData(UserPrincipal userPrincipal) {
        return Try.of(() -> this.expensePersistence.getExpensesGroupByDateChartData(userPrincipal)
                        .stream()
                        .map(chartDataMapper::toChartData)
                        .toList())
                .getOrElseThrow(MaBillsServiceException::new);
    }

}
