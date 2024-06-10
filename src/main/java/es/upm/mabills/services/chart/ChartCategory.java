package es.upm.mabills.services.chart;

import es.upm.mabills.exceptions.InvalidRequestException;

public enum ChartCategory {
    EXPENSES,
    INCOMES,
    EXPENSE_INCOME_SERIES;

    public static ChartCategory fromString(String value) {
        for (ChartCategory type : ChartCategory.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new InvalidRequestException("Invalid chart category type");
    }
}
