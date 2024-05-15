package es.upm.mabills.persistence.exception_mappers;

import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.model.CreditCard;

import java.util.function.Predicate;

public class CreditCardDataIntegrityExceptionMapper {
    private static final Predicate<Throwable> BANK_ACCOUNT_NOT_FOUND = e -> e.getMessage().contains("BANK_ACCOUNT_ID");
    private static final Predicate<Throwable> USER_NOT_FOUND = e -> e.getMessage().contains("USER_ID");

    public static RuntimeException map(Throwable e, String username, CreditCard creditCard) {
        if(BANK_ACCOUNT_NOT_FOUND.test(e)) {
            return new BankAccountNotFoundException(creditCard.getBankAccount().getIban());
        } else if (USER_NOT_FOUND.test(e)) {
            return new UserNotFoundException(username);
        }
        return new RuntimeException(e);
    }

    private CreditCardDataIntegrityExceptionMapper() { }
}
