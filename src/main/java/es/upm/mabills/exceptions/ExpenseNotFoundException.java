package es.upm.mabills.exceptions;

public class ExpenseNotFoundException extends RuntimeException {
    public ExpenseNotFoundException() {
        super("Expense not found");
    }

    public ExpenseNotFoundException(String uuid) {
        super("Expense with ID: " + uuid + " not found");
    }
}
