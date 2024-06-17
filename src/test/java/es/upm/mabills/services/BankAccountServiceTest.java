package es.upm.mabills.services;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.exceptions.BankAccountAlreadyExistsException;
import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.MaBillsUnexpectedException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTestConfig
class BankAccountServiceTest {
    private static final String NOT_FOUND_USER = "notFoundUser";
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String IBAN = "ES7921000813610123456789";

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

    @Test
    void testCreateBankAccountSuccess() {
        BankAccount bankAccount = BankAccount.builder().iban(IBAN).build();
        BankAccountEntity bankAccountEntity = BankAccountEntity.builder()
                .iban(bankAccount.getIban())
                .build();
        when(bankAccountPersistence.createBankAccount(ENCODED_PASSWORD_USER_PRINCIPAL, bankAccount)).thenReturn(bankAccountEntity);
        assertEquals(bankAccountEntity.getIban(), bankAccountService.createBankAccount(ENCODED_PASSWORD_USER_PRINCIPAL, bankAccount).getIban());
    }

    @Test
    void testCreateBankAccountThrowsException() {
        BankAccount bankAccount = BankAccount.builder().iban(IBAN).build();
        when(bankAccountPersistence.createBankAccount(ENCODED_PASSWORD_USER_PRINCIPAL, bankAccount)).thenThrow(new RuntimeException());
        assertThrows(BankAccountAlreadyExistsException.class, () -> bankAccountService.createBankAccount(ENCODED_PASSWORD_USER_PRINCIPAL, bankAccount));
    }

    @Test
    void testDeleteBankAccountSuccess() {
        bankAccountService.deleteBankAccount(ENCODED_PASSWORD_USER_PRINCIPAL ,"uuid");
        verify(bankAccountPersistence).deleteBankAccount(ENCODED_PASSWORD_USER_PRINCIPAL, "uuid");
    }

    @Test
    void testDeleteBankAccountDBException() {
        doThrow(RuntimeException.class).when(bankAccountPersistence).deleteBankAccount(any(), anyString());
        assertThrows(MaBillsUnexpectedException.class, () -> bankAccountService.deleteBankAccount(ENCODED_PASSWORD_USER_PRINCIPAL, "uuid"));
    }

    @Test
    void testDeleteBankAccountForAnotherUserThrowsException() {
        doThrow(BankAccountNotFoundException.class).when(bankAccountPersistence).deleteBankAccount(any(), anyString());
        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.deleteBankAccount(ENCODED_PASSWORD_USER_PRINCIPAL, "uuid"));
    }
}
