package es.upm.mabills.exceptions;

public class BankAccountAlreadyExistsException extends RuntimeException {
    public BankAccountAlreadyExistsException(String username, String iban) {
        super(String.format("Bank account with IBAN %s already exists for user %s", iban, username));
    }
}
