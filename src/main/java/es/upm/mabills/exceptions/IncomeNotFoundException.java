package es.upm.mabills.exceptions;

public class IncomeNotFoundException extends RuntimeException {
    public IncomeNotFoundException() {
        super("Income not found");
    }

    public IncomeNotFoundException(String uuid) {
        super("Income with uuid " + uuid + " not found");
    }
}
