package es.upm.mabills.services;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.persistence.BankAccountPersistence;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
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

    private final UserEntity userEntity = UserEntity.builder()
        .username(ENCODED_PASSWORD_USER)
        .build();

    @BeforeEach
    void setUp() {
        when(bankAccountPersistence.findByIbanAndUserId(NOT_FOUND_USER)).thenReturn(List.of());
        when(bankAccountPersistence.findByIbanAndUserId(ENCODED_PASSWORD_USER)).thenReturn(List.of(
            BankAccountEntity.builder()
                .user(userEntity)
                .creditCards(List.of(
                    CreditCardEntity.builder()
                            .user(userEntity)
                            .build()
                ))
                .build()
        ));
    }

    @Test
    void testFindByIbanAndUserIdNotFoundUser() {
        assertTrue(bankAccountService.findByIbanAndUserId(NOT_FOUND_USER).isEmpty());
    }

    @Test
    void testFindByIbanAndUserId() {
        List<BankAccount> bankAccounts = bankAccountService.findByIbanAndUserId(ENCODED_PASSWORD_USER);
        assertFalse(bankAccountService.findByIbanAndUserId(ENCODED_PASSWORD_USER).isEmpty());
        assertEquals(1, bankAccounts.size());
        assertEquals(ENCODED_PASSWORD_USER, bankAccounts.get(0).getUser().getUsername());
        assertEquals(1, bankAccounts.get(0).getCreditCards().size());
    }
}
