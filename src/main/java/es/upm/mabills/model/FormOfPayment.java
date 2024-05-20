package es.upm.mabills.model;

import lombok.Getter;

@Getter
public enum FormOfPayment {
    CARD("Card"),
    CASH("Cash"),
    BANK_TRANSFER("Bank Transfer");

    private final String value;

    FormOfPayment(String value) {
        this.value = value;
    }
}
