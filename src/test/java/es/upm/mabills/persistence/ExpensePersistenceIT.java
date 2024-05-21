package es.upm.mabills.persistence;


import es.upm.mabills.TestConfig;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class ExpensePersistenceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final UserPrincipal NOT_FOUND_USER_PRINCIPAL = UserPrincipal.builder()
            .id(0)
            .username("notFoundUser")
            .build();

    @Autowired
    private ExpensePersistence expensePersistence;

    @Autowired
    private UserRepository userRepository;

    private UserPrincipal encodedUserPrincipal;

    @BeforeEach
    void setUp() {
        encodedUserPrincipal = UserPrincipal.builder()
                .id(userRepository.findByUsername(ENCODED_PASSWORD_USER).getId())
                .username(ENCODED_PASSWORD_USER)
                .build();
    }

    @Test
    void testFindExpenseByUserId() {
        assertFalse(expensePersistence.findExpenseByUserId(encodedUserPrincipal).isEmpty());
    }

    @Test
    void testFindExpenseByUserIdNotFound() {
        assertTrue(expensePersistence.findExpenseByUserId(NOT_FOUND_USER_PRINCIPAL).isEmpty());
    }

}
