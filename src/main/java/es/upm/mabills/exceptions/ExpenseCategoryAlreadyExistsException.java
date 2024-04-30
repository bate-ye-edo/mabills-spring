package es.upm.mabills.exceptions;

public class ExpenseCategoryAlreadyExistsException extends RuntimeException {
    public ExpenseCategoryAlreadyExistsException(String expenseCategoryName) {
        super("Expense category with name " + expenseCategoryName + " already exists for the current user");
    }
}
