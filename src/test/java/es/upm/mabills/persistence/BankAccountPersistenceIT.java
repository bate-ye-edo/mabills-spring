package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import es.upm.mabills.model.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private UserPersistence userPersistence;

    private UserPrincipal encodedUserPrincipal;
    private UserPrincipal notFoundUserPrincipal;

    @BeforeEach
    void setUp() {
        encodedUserPrincipal = UserPrincipal.builder()
                .id(userPersistence.findUserByUsername(ENCODED_PASSWORD_USER).getId())
                .username(ENCODED_PASSWORD_USER)
                .build();
        notFoundUserPrincipal = UserPrincipal.builder()
                .id(0)
                .username(NOT_FOUND_USER)
                .build();
    }


    @Test
    void testFindBankAccountsForUser() {
        assertFalse(bankAccountPersistence.findBankAccountsForUser(encodedUserPrincipal).isEmpty());
    }

    @Test
    void testFindBankAccountsForUserNotFound() {
        assertTrue(bankAccountPersistence.findBankAccountsForUser(notFoundUserPrincipal).isEmpty());
    }
}
