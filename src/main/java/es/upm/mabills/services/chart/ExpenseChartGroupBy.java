package es.upm.mabills.services.chart;

import es.upm.mabills.exceptions.InvalidRequestException;

import java.util.Objects;

public enum ExpenseChartGroupBy {
    EXPENSE_DATE,
    EXPENSE_CATEGORY;

    public static ExpenseChartGroupBy fromString(String value) {
        if(Objects.isNull(value)) {
            return EXPENSE_DATE;
        }
        for (ExpenseChartGroupBy type : ExpenseChartGroupBy.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new InvalidRequestException("Invalid expense chart group by type");
    }
}
