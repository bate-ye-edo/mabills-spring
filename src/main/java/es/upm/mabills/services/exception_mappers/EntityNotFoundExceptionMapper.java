package es.upm.mabills.services.exception_mappers;

import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.model.CreditCard;

import java.util.function.Predicate;

public class EntityNotFoundExceptionMapper {
    private static final Predicate<Throwable> BANK_ACCOUNT_NOT_FOUND = e -> e.getMessage().contains("BankAccountEntity");
    private static final Predicate<Throwable> USER_NOT_FOUND = e -> e.getMessage().contains("UserEntity");

    public static RuntimeException map(Throwable e, String username, CreditCard creditCard) {
        if(BANK_ACCOUNT_NOT_FOUND.test(e)) {
            return new BankAccountNotFoundException(creditCard.getBankAccount().getIban());
        } else if (USER_NOT_FOUND.test(e)) {
            return new UserNotFoundException(username);
        }
        return (RuntimeException) e;
    }

    private EntityNotFoundExceptionMapper() { }
}
