package es.upm.mabills.services.charts;

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
    public Chart getChart(UserPrincipal userPrincipal, String groupBy) {
        return Chart.builder()
                .data(this.getChartByGroupByType(userPrincipal, ExpenseChartGroupBy.fromString(groupBy)))
                .build();
    }

    private List<ChartData> getExpensesGroupByDateChartData(UserPrincipal userPrincipal) {
        return Try.of(() -> this.expensePersistence.getExpensesGroupByDateChartData(userPrincipal)
                        .stream()
                        .map(chartDataMapper::toChartData)
                        .toList())
                .getOrElseThrow(() -> new MaBillsServiceException());
    }

    private List<ChartData> getExpensesGroupByCategoryChartData(UserPrincipal userPrincipal) {
        return  Try.of(() -> this.expensePersistence.getExpensesGroupByCategoryChartData(userPrincipal))
                .getOrElseThrow(() -> new MaBillsServiceException());
    }

    private List<ChartData> getExpensesGroupByCreditCardChartData(UserPrincipal userPrincipal) {
        return Try.of(() -> this.expensePersistence.getExpensesGroupByCreditCardChartData(userPrincipal))
                .getOrElseThrow(() -> new MaBillsServiceException());
    }


    private List<ChartData> getExpensesGroupByBankAccountChartData(UserPrincipal userPrincipal) {
        return Try.of(() -> this.expensePersistence.getExpensesGroupByBankAccountChartData(userPrincipal))
                .getOrElseThrow(() -> new MaBillsServiceException());
    }

    private List<ChartData> getExpensesGroupByFOPChartData(UserPrincipal userPrincipal) {
        return Try.of(() -> this.expensePersistence.getExpensesGroupByFOPChartData(userPrincipal))
                .getOrElseThrow(() -> new MaBillsServiceException());
    }

    private List<ChartData> getChartByGroupByType(UserPrincipal userPrincipal, ExpenseChartGroupBy expenseChartGroupBy) {
        return switch (expenseChartGroupBy) {
            case EXPENSE_CATEGORY -> getExpensesGroupByCategoryChartData(userPrincipal);
            case EXPENSE_DATE -> getExpensesGroupByDateChartData(userPrincipal);
            case EXPENSE_CREDIT_CARD -> getExpensesGroupByCreditCardChartData(userPrincipal);
            case EXPENSE_FORM_OF_PAYMENT -> getExpensesGroupByFOPChartData(userPrincipal);
            case EXPENSE_BANK_ACCOUNT -> getExpensesGroupByBankAccountChartData(userPrincipal);
        };
    }


}
