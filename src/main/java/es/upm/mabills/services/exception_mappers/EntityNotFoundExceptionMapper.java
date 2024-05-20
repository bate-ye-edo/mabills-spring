package es.upm.mabills.services.exception_mappers;

import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Predicate;

public class EntityNotFoundExceptionMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityNotFoundExceptionMapper.class);
    private static final Predicate<Throwable> BANK_ACCOUNT_NOT_FOUND = e -> e.getMessage().contains("BankAccountEntity");
    private static final Predicate<Throwable> USER_NOT_FOUND = e -> e.getMessage().contains("UserEntity");

    private static final Map<Predicate<Throwable>, RuntimeException> EXCEPTION_MAPPER = Map.ofEntries(
            Map.entry(BANK_ACCOUNT_NOT_FOUND, new BankAccountNotFoundException()),
            Map.entry(USER_NOT_FOUND, new UserNotFoundException())
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
