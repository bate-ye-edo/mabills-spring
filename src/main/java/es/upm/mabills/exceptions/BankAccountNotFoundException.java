package es.upm.mabills.exceptions;

public class BankAccountNotFoundException extends RuntimeException {
    private static final String BANK_ACCOUNT_NOT_FOUND = "Bank account not found";
    public BankAccountNotFoundException(String iban) {
        super("Bank account with IBAN " + iban + " not found");
    }

    public BankAccountNotFoundException() {
        super(BANK_ACCOUNT_NOT_FOUND);
    }

}
