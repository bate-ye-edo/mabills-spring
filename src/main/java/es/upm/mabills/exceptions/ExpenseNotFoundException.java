package es.upm.mabills.exceptions;

public class ExpenseNotFoundException extends RuntimeException {
    public ExpenseNotFoundException() {
        super("Expense not found");
    }
}
