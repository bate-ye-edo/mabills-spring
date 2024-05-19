package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class BankAccountPersistenceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String NOT_FOUND_USER = "notFoundUser";
    private static final String NEW_IBAN = "ES7921000813610123456100";
    private static final String EXIST_IBAN = "ES004120003120034012";
    private static final String TO_DELETE_IBAN = "to_delete_bank_account";
    private static final String TO_DELETE_IBAN_WITH_CREDIT_CARD = "to_delete_bank_account_entity_with_credit_card";
    private static final String CREDIT_CARD_NUMBER_WITH_DELETED_BANK_ACCOUNT = "bank_account_will_be_deleted";
    private static final String OTHER_USER = "otherUser";

    @Autowired
    private BankAccountPersistence bankAccountPersistence;

    @Autowired
    private UserPersistence userPersistence;

    @Autowired
    private CreditCardPersistence creditCardPersistence;

    private UserPrincipal encodedUserPrincipal;
    private UserPrincipal notFoundUserPrincipal;
    private UserPrincipal otherUserPrincipal;

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
        otherUserPrincipal = UserPrincipal.builder()
                .id(userPersistence.findUserByUsername(OTHER_USER).getId())
                .username(OTHER_USER)
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

    @Test
    void testDeleteBankAccountSuccess() {
        String uuid = bankAccountPersistence.findBankAccountsForUser(encodedUserPrincipal)
                .stream()
                .filter(ba -> ba.getIban().equals(TO_DELETE_IBAN))
                .toList()
                .get(0)
                .getUuid()
                .toString();
        bankAccountPersistence.deleteBankAccount(encodedUserPrincipal, uuid);
        assertTrue(bankAccountPersistence.findBankAccountsForUser(encodedUserPrincipal)
                .stream()
                .filter(ba->ba.getIban().equals(TO_DELETE_IBAN))
                .toList()
                .isEmpty());
    }

    @Test
    void testDeleteBankAccountForAnotherUserThrowsException() {
        String uuid = bankAccountPersistence.findBankAccountsForUser(encodedUserPrincipal)
                .get(0)
                .getUuid().toString();
        assertThrows(BankAccountNotFoundException.class, () -> bankAccountPersistence.deleteBankAccount(otherUserPrincipal, uuid));
    }

    @Test
    void testDeleteBankAccountWithCreditCardSuccess() {
        String uuid = bankAccountPersistence.findBankAccountsForUser(encodedUserPrincipal)
                .stream()
                .filter(ba -> ba.getIban().equals(TO_DELETE_IBAN_WITH_CREDIT_CARD))
                .toList()
                .get(0)
                .getUuid()
                .toString();
        bankAccountPersistence.deleteBankAccount(encodedUserPrincipal, uuid);
        CreditCardEntity creditCardEntity = creditCardPersistence.findCreditCardsForUser(encodedUserPrincipal)
                .stream()
                .filter(cd -> cd.getCreditCardNumber().equals(CREDIT_CARD_NUMBER_WITH_DELETED_BANK_ACCOUNT))
                .toList()
                .get(0);
        assertNotNull(creditCardEntity);
        assertNull(creditCardEntity.getBankAccount());
    }

}
