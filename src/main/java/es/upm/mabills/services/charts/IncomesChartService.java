package es.upm.mabills.services.charts;

import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.mappers.ChartDataMapper;
import es.upm.mabills.model.Chart;
import es.upm.mabills.model.ChartData;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.IncomePersistence;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("incomesChartService")
public class IncomesChartService implements ChartService {
    private final IncomePersistence incomePersistence;
    private final ChartDataMapper chartDataMapper;

    @Autowired
    public IncomesChartService(IncomePersistence incomePersistence, ChartDataMapper chartDataMapper) {
        this.incomePersistence = incomePersistence;
        this.chartDataMapper = chartDataMapper;
    }

    @Override
    public Chart getChart(UserPrincipal userPrincipal, String groupBy) {
        return Chart.builder()
                .data(this.getChartByGroupByType(userPrincipal, IncomeChartGroupBy.fromString(groupBy)))
                .build();
    }

    private List<ChartData> getChartByGroupByType(UserPrincipal userPrincipal, IncomeChartGroupBy groupBy) {
        return switch (groupBy){
            case INCOME_DATE -> getIncomesGroupByDateChartData(userPrincipal);
            case INCOME_BANK_ACCOUNT -> getIncomesGroupByBankAccountChartData(userPrincipal);
            case INCOME_CREDIT_CARD -> getIncomesGroupByCreditCardChartData(userPrincipal);
        };
    }

    private List<ChartData> getIncomesGroupByCreditCardChartData(UserPrincipal userPrincipal) {
        return Try.of(() -> this.incomePersistence.getIncomesGroupByCreditCardChartData(userPrincipal))
                .getOrElseThrow(() -> new MaBillsServiceException());
    }

    private List<ChartData> getIncomesGroupByBankAccountChartData(UserPrincipal userPrincipal) {
        return Try.of(() -> this.incomePersistence.getIncomesGroupByBankAccountChartData(userPrincipal))
                .getOrElseThrow(() -> new MaBillsServiceException());
    }

    private List<ChartData> getIncomesGroupByDateChartData(UserPrincipal userPrincipal) {
        return Try.of(() -> this.incomePersistence.getIncomesGroupByDateChartData(userPrincipal)
                        .stream()
                        .map(chartDataMapper::toChartData)
                        .toList())
                .getOrElseThrow(() -> new MaBillsServiceException());
    }
}
