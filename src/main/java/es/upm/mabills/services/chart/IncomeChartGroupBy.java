package es.upm.mabills.services.chart;

import es.upm.mabills.exceptions.InvalidRequestException;

public enum IncomeChartGroupBy {
    INCOME_DATE,
    INCOME_CREDIT_CARD,
    INCOME_BANK_ACCOUNT;
    public static IncomeChartGroupBy fromString(String value) {
        if(value == null) {
            return INCOME_DATE;
        }
        for (IncomeChartGroupBy type : IncomeChartGroupBy.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new InvalidRequestException("Invalid income chart group by type");
    }
}
