package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class CreditCardPersistenceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String NOT_FOUND_USER = "notFound";

    @Autowired
    private CreditCardPersistence creditCardPersistence;

    @Test
    void testFindCreditCardsByUserName() {
        assertFalse(creditCardPersistence.findCreditCardsByUserName(ENCODED_PASSWORD_USER).isEmpty());
    }

    @Test
    void testFindCreditCardsByUserNameNotFound() {
        assertTrue(creditCardPersistence.findCreditCardsByUserName(NOT_FOUND_USER).isEmpty());
    }
}
