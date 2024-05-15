package es.upm.mabills.exceptions;

public class CreditCardAlreadyExistsException extends RuntimeException {
    public CreditCardAlreadyExistsException(String username, String creditCardNumber) {
        super("Credit card with number " + creditCardNumber + " already exists for user " + username);
    }
}
