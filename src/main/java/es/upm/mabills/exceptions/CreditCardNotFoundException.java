package es.upm.mabills.exceptions;

public class CreditCardNotFoundException extends RuntimeException {
    public CreditCardNotFoundException(String message) {
        super("Credit card with id: "+message+" not found");
    }
}
