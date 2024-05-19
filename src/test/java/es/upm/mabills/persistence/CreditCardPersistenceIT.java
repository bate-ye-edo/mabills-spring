package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import es.upm.mabills.exceptions.CreditCardAlreadyExistsException;
import es.upm.mabills.exceptions.CreditCardNotFoundException;
import es.upm.mabills.model.BankAccount;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class CreditCardPersistenceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String NOT_FOUND_USER = "notFound";
    private static final String NEW_CREDIT_CARD_NUMBER = "123456789012";
    private static final String NOT_EXISTS_BANK_ACCOUNT_IBAN = "ES0000000000000000000000";
    private static final String NOT_EXISTS_BANK_ACCOUNT_UUID = "00000000-0000-0000-0000-000000000001";
    private static final String ALREADY_EXISTS_CREDIT_CARD_NUMBER = "004120003120034012";
    private static final String OTHER_USER = "otherUser";
    private static final String TO_DELETE_CREDIT_CARD_NUMBER = "005130013120034012";

    @Autowired
    private CreditCardPersistence creditCardPersistence;

    @Autowired
    private UserPersistence userPersistence;

    private UserPrincipal encodedUserPrincipal;

    @BeforeEach
    void setUpAll() {
        encodedUserPrincipal = UserPrincipal.builder()
                .id(userPersistence.findUserByUsername(ENCODED_PASSWORD_USER).getId())
                .username(ENCODED_PASSWORD_USER)
                .build();
    }

    @Test
    void testFindCreditCardsForUser() {
        assertFalse(creditCardPersistence.findCreditCardsForUser(encodedUserPrincipal).isEmpty());
    }

    @Test
    void testFindCreditCardsForUserNotFound() {
        assertTrue(creditCardPersistence.findCreditCardsForUser(getNotFoundUserPrincipal()).isEmpty());
    }

    @Test
    @Transactional
    void testCreateCreditCard() {
        CreditCardEntity creditCardEntity = creditCardPersistence.createCreditCard(encodedUserPrincipal, buildNewCreditCard());
        assertNotNull(creditCardEntity);
        assertEquals(ENCODED_PASSWORD_USER, creditCardEntity.getUser().getUsername());
        assertEquals(NEW_CREDIT_CARD_NUMBER, creditCardEntity.getCreditCardNumber());
    }

    @Test
    void testCreateCreditCardAlreadyExists() {
        CreditCard creditCard = getAlreadyExistsCreditCard();
        assertThrows(CreditCardAlreadyExistsException.class, () -> creditCardPersistence.createCreditCard(encodedUserPrincipal, creditCard));
    }

    @Test
    void testCreateCreditCardUserNotFound() {
        CreditCard creditCard = buildNewCreditCard();
        UserPrincipal userPrincipal = getNotFoundUserPrincipal();
        assertThrows(DataIntegrityViolationException.class, () -> creditCardPersistence.createCreditCard(userPrincipal, creditCard));
    }

    @Test
    void testCreateCreditCardBankAccountNotFound() {
        CreditCard creditCard = CreditCard.builder()
                .creditCardNumber(NEW_CREDIT_CARD_NUMBER)
                .bankAccount(BankAccount.builder()
                        .uuid(NOT_EXISTS_BANK_ACCOUNT_UUID)
                        .iban(NOT_EXISTS_BANK_ACCOUNT_IBAN)
                        .build())
                .build();
        assertThrows(DataIntegrityViolationException.class, () -> creditCardPersistence.createCreditCard(encodedUserPrincipal, creditCard));
    }

    @Test
    void testDeleteCreditCard() {
        int userId = getOtherUserId();
        String creditCardUuid = getToDeleteCreditCardUuid(userId);
        UserPrincipal otherUserPrincipal = buildOtherUserPrincipal(userId);
        creditCardPersistence.deleteCreditCard(otherUserPrincipal, creditCardUuid);
        assertTrue(creditCardPersistence.findCreditCardsForUser(otherUserPrincipal).isEmpty());
    }

    @Test
    void testDeleteCreditCardForOtherUserFails() {
        int userId = getOtherUserId();
        UserPrincipal otherUserPrincipal = buildOtherUserPrincipal(userId);
        String encodedUserCreditCardUuid = getEncodedUserPrincipalFirstCreditCardUuid();
        assertThrows(CreditCardNotFoundException.class, () -> creditCardPersistence.deleteCreditCard(otherUserPrincipal, encodedUserCreditCardUuid));
    }

    private int getOtherUserId() {
        return userPersistence.findUserByUsername(OTHER_USER).getId();
    }

    private String getToDeleteCreditCardUuid(int userId) {
        return creditCardPersistence.findCreditCardsForUser(buildOtherUserPrincipal(userId))
                .stream()
                .filter(creditCardEntity -> creditCardEntity.getCreditCardNumber().equals(TO_DELETE_CREDIT_CARD_NUMBER))
                .findFirst()
                .map(CreditCardEntity::getUuid)
                .map(UUID::toString)
                .orElse(null);
    }

    private UserPrincipal buildOtherUserPrincipal(int userId) {
        return UserPrincipal.builder()
                .id(userId)
                .username(OTHER_USER)
                .build();
    }

    private CreditCard buildNewCreditCard() {
        return CreditCard.builder()
                .creditCardNumber(NEW_CREDIT_CARD_NUMBER)
                .build();
    }

    private CreditCard getAlreadyExistsCreditCard() {
        return CreditCard.builder()
                .creditCardNumber(ALREADY_EXISTS_CREDIT_CARD_NUMBER)
                .build();
    }

    private UserPrincipal getNotFoundUserPrincipal() {
        return UserPrincipal.builder()
                .id(0)
                .username(NOT_FOUND_USER)
                .build();
    }

    private String getEncodedUserPrincipalFirstCreditCardUuid() {
        return creditCardPersistence.findCreditCardsForUser(encodedUserPrincipal)
                .get(0)
                .getUuid()
                .toString();
    }
}
