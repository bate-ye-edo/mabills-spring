package es.upm.mabills.services.charts;

import es.upm.mabills.exceptions.InvalidRequestException;

import java.util.Objects;

public enum ExpenseChartGroupBy {
    EXPENSE_DATE,
    EXPENSE_CATEGORY,
    EXPENSE_CREDIT_CARD,
    EXPENSE_BANK_ACCOUNT,
    EXPENSE_FORM_OF_PAYMENT;
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
