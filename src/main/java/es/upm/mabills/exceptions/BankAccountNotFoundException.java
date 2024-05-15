package es.upm.mabills.exceptions;

public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException(String iban) {
        super("Bank account with IBAN " + iban + " not found");
    }
}
