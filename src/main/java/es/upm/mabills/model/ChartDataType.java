package es.upm.mabills.model;

import es.upm.mabills.exceptions.InvalidRequestException;

public enum ChartDataType {
    EXPENSES,
    INCOMES;

    public static ChartDataType fromString(String value) {
        for (ChartDataType type : ChartDataType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new InvalidRequestException("Invalid data type");
    }
}
