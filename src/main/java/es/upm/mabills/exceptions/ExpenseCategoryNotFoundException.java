package es.upm.mabills.exceptions;

import java.util.UUID;

public class ExpenseCategoryNotFoundException extends RuntimeException {
    public ExpenseCategoryNotFoundException(UUID uuid) {
        super("Expense category with ID: " + uuid + " not found");
    }

    public ExpenseCategoryNotFoundException() {
        super("Expense category not found");
    }
}
