package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class BankAccountPersistenceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String NOT_FOUND_USER = "notFoundUser";
    private static final String NEW_IBAN = "ES7921000813610123456100";
    private static final String EXIST_IBAN = "ES004120003120034012";

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

    @Test
    void testCreateBankAccountSuccess() {
        BankAccountEntity bankAccountEntity = bankAccountPersistence.createBankAccount(encodedUserPrincipal, BankAccount.builder().iban(NEW_IBAN).build());
        assertNotNull(bankAccountEntity);
        assertEquals(NEW_IBAN, bankAccountEntity.getIban());
    }

    @Test
    void testCreateBankAccountAlreadyExists() {
        BankAccount bankAccount = BankAccount.builder().iban(EXIST_IBAN).build();
        assertThrows(DataIntegrityViolationException.class, ()->bankAccountPersistence.createBankAccount(encodedUserPrincipal, bankAccount));
    }
}
