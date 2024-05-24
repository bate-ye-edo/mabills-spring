package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.MaBillsServiceException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.BankAccountEntity;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import es.upm.mabills.persistence.entity_dependent_managers.EntityDependentManager;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
import es.upm.mabills.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfig
class BankAccountPersistenceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String NOT_FOUND_USER = "notFoundUser";
    private static final String NEW_IBAN = "ES7921000813610123456100";
    private static final String EXIST_IBAN = "ES004120003120034012";
    private static final String TO_DELETE_IBAN = "to_delete_bank_account";
    private static final String TO_DELETE_IBAN_WITH_CREDIT_CARD = "to_delete_bank_account_entity_with_credit_card";
    private static final String TO_DELETE_IBAN_WITH_CREDIT_CARD_AND_EXPENSE = "to_delete_bank_account_entity_with_credit_card_and_expense";
    private static final String CREDIT_CARD_NUMBER_WITH_DELETED_BANK_ACCOUNT = "bank_account_will_be_deleted";
    private static final String CREDIT_CARD_NUMBER_WITH_DELETED_BANK_ACCOUNT_AND_EXPENSE = "bank_account_will_be_deleted_and_expense";
    private static final String OTHER_USER = "otherUser";
    private static final String RANDOM_UUID = UUID.randomUUID().toString();

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

    @Test
    void testDeleteBankAccountWithCreditCardAndExpenseSuccess() {
        String uuid = bankAccountPersistence.findBankAccountsForUser(encodedUserPrincipal)
                .stream()
                .filter(ba -> ba.getIban().equals(TO_DELETE_IBAN_WITH_CREDIT_CARD_AND_EXPENSE))
                .toList()
                .get(0)
                .getUuid()
                .toString();
        bankAccountPersistence.deleteBankAccount(encodedUserPrincipal, uuid);
        CreditCardEntity creditCardEntity = creditCardPersistence.findCreditCardsForUser(encodedUserPrincipal)
                .stream()
                .filter(cd -> cd.getCreditCardNumber().equals(CREDIT_CARD_NUMBER_WITH_DELETED_BANK_ACCOUNT_AND_EXPENSE))
                .toList()
                .get(0);
        assertNotNull(creditCardEntity);
        assertNull(creditCardEntity.getBankAccount());
    }

    @Test
    void testDeleteBankAccountThrowsDbUnknownException() {
        BankAccountRepository repository = mock(BankAccountRepository.class);
        EntityDependentManager entityDependentManager = mock(EntityDependentManager.class);
        BankAccountPersistence bankAccountPersistence1 = createBankAccountPersistenceWithMocks(repository, entityDependentManager);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(any());
        doNothing().when(entityDependentManager).decouple(any());
        when(repository.findByUserIdAndUuid(anyInt(), any())).thenReturn(BankAccountEntity.builder().uuid(UUID.fromString(RANDOM_UUID)).build());
        assertThrows(MaBillsServiceException.class, () -> bankAccountPersistence1.deleteBankAccount(encodedUserPrincipal, RANDOM_UUID));
    }

    @Test
    void testDeleteBankAccountThrowsNullPointerException() {
        BankAccountRepository repository = mock(BankAccountRepository.class);
        EntityDependentManager entityDependentManager = mock(EntityDependentManager.class);
        BankAccountPersistence bankAccountPersistence1 = createBankAccountPersistenceWithMocks(repository, entityDependentManager);
        doThrow(NullPointerException.class).when(entityDependentManager).decouple(any());
        assertThrows(BankAccountNotFoundException.class, () -> bankAccountPersistence1.deleteBankAccount(encodedUserPrincipal, RANDOM_UUID));
    }


    @Test
    void testFindBankAccountByUserAndUuidNull() {
        BankAccountEntity bankAccountEntity = bankAccountPersistence.findBankAccountByUserAndUuid(encodedUserPrincipal, RANDOM_UUID);
        assertNull(bankAccountEntity);
    }

    @Test
    void testFindBankAccountByUserAndUuid() {
        BankAccountEntity bankAccountEntity = bankAccountPersistence.findBankAccountByUserAndUuid(encodedUserPrincipal,
                bankAccountPersistence.findBankAccountsForUser(encodedUserPrincipal).get(0).getUuid().toString());
        assertNotNull(bankAccountEntity);
    }

    private BankAccountPersistence createBankAccountPersistenceWithMocks(BankAccountRepository mockRepository,
                                                                         EntityDependentManager mockEntityDependantManager) {
        return new BankAccountPersistence(mockRepository, mock(UserRepository.class), mockEntityDependantManager);
    }
}
