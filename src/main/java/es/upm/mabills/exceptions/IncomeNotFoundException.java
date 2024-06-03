package es.upm.mabills.exceptions;

public class IncomeNotFoundException extends RuntimeException {
    public IncomeNotFoundException() {
        super("Income not found");
    }
}
