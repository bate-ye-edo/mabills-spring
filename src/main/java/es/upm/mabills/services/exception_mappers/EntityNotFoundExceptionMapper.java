package es.upm.mabills.services.exception_mappers;

import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.CreditCardNotFoundException;
import es.upm.mabills.exceptions.ExpenseCategoryNotFoundException;
import es.upm.mabills.exceptions.ExpenseNotFoundException;
import es.upm.mabills.exceptions.IncomeNotFoundException;
import es.upm.mabills.exceptions.UserNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.Map;
import java.util.function.Predicate;

public class EntityNotFoundExceptionMapper {
    private static final Logger LOGGER = LogManager.getLogger(EntityNotFoundExceptionMapper.class);
    private static final Predicate<Throwable> BANK_ACCOUNT_NOT_FOUND = e -> e.getMessage().contains("BankAccountEntity");
    private static final Predicate<Throwable> USER_NOT_FOUND = e -> e.getMessage().contains("UserEntity");
    private static final Predicate<Throwable> CREDIT_CARD_NOT_FOUND = e -> e.getMessage().contains("CreditCardEntity");
    private static final Predicate<Throwable> EXPENSE_CATEGORY_NOT_FOUND = e -> e.getMessage().contains("ExpenseCategoryEntity");
    private static final Predicate<Throwable> EXPENSE_NOT_FOUND = e -> e.getMessage().contains("ExpenseEntity");
    private static final Predicate<Throwable> INCOME_NOT_FOUND = e -> e.getMessage().contains("IncomeEntity");

    private static final Map<Predicate<Throwable>, RuntimeException> EXCEPTION_MAPPER = Map.ofEntries(
            Map.entry(BANK_ACCOUNT_NOT_FOUND, new BankAccountNotFoundException()),
            Map.entry(USER_NOT_FOUND, new UserNotFoundException()),
            Map.entry(CREDIT_CARD_NOT_FOUND, new CreditCardNotFoundException()),
            Map.entry(EXPENSE_CATEGORY_NOT_FOUND, new ExpenseCategoryNotFoundException()),
            Map.entry(EXPENSE_NOT_FOUND, new ExpenseNotFoundException()),
            Map.entry(INCOME_NOT_FOUND, new IncomeNotFoundException())
    );

    public static RuntimeException map(Throwable e) {
        LOGGER.warn("Mapping exception: {}", e.getMessage());
        for(Map.Entry<Predicate<Throwable>, RuntimeException> entry : EXCEPTION_MAPPER.entrySet()) {
            if (entry.getKey().test(e)) {
                return entry.getValue();
            }
        }
        return (RuntimeException) e;
    }

    private EntityNotFoundExceptionMapper() { }
}
