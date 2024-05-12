package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class BankAccountPersistenceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String NOT_FOUND_USER = "notFoundUser";
    @Autowired
    private BankAccountPersistence bankAccountPersistence;

    @Test
    void testFindBankAccountsByUsername() {
        assertFalse(bankAccountPersistence.findBankAccountsByUsername(ENCODED_PASSWORD_USER).isEmpty());
    }

    @Test
    void testFindBankAccountsByUsernameNotFound() {
        assertTrue(bankAccountPersistence.findBankAccountsByUsername(NOT_FOUND_USER).isEmpty());
    }
}
