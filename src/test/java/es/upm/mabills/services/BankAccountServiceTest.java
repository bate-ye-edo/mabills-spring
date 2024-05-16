package es.upm.mabills.services;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.BankAccountPersistence;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@UnitTestConfig
class BankAccountServiceTest {
    private static final String NOT_FOUND_USER = "notFoundUser";
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";

    @Autowired
    private BankAccountService bankAccountService;

    @MockBean
    private BankAccountPersistence bankAccountPersistence;

    private static final UserPrincipal NOT_FOUND_USER_PRINCIPAL = UserPrincipal.builder()
            .id(2)
            .username(NOT_FOUND_USER)
            .build();

    private static final UserPrincipal ENCODED_PASSWORD_USER_PRINCIPAL = UserPrincipal.builder()
            .id(1)
            .username(ENCODED_PASSWORD_USER)
            .build();

    @BeforeEach
    void setUp() {
        when(bankAccountPersistence.findBankAccountsForUser(NOT_FOUND_USER_PRINCIPAL)).thenReturn(List.of());
        when(bankAccountPersistence.findBankAccountsForUser(ENCODED_PASSWORD_USER_PRINCIPAL)).thenReturn(List.of(
            BankAccountEntity.builder()
                .user(UserEntity.builder()
                        .id(ENCODED_PASSWORD_USER_PRINCIPAL.getId())
                        .username(ENCODED_PASSWORD_USER_PRINCIPAL.getUsername())
                        .build())
                .build()
        ));
    }

    @Test
    void testFindBankAccountsByIbanAndUserIdNotFoundUser() {
        assertTrue(bankAccountService.findBankAccountsForUser(NOT_FOUND_USER_PRINCIPAL).isEmpty());
    }

    @Test
    void testFindBankAccountsForUser() {
        List<BankAccount> bankAccounts = bankAccountService.findBankAccountsForUser(ENCODED_PASSWORD_USER_PRINCIPAL);
        assertFalse(bankAccountService.findBankAccountsForUser(ENCODED_PASSWORD_USER_PRINCIPAL).isEmpty());
        assertEquals(1, bankAccounts.size());
    }
}
