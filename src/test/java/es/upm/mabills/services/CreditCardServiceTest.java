package es.upm.mabills.services;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.exceptions.BankAccountNotFoundException;
import es.upm.mabills.exceptions.CreditCardAlreadyExistsException;
import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.model.CreditCard;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.CreditCardPersistence;
import es.upm.mabills.persistence.entities.CreditCardEntity;
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
import static org.mockito.Mockito.when;

@UnitTestConfig
class CreditCardServiceTest {
    private static final String NOT_FOUND_USER = "notFoundUser";
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final int ENCODED_USER_ID = 1111;
    private static final String NEW_CREDIT_CARD_NUMBER = "123456789012";
    private static final String NOT_EXISTS_BANK_ACCOUNT_UUID = "000000-0000-0000-0000-000000000001";
    private static final String NOT_EXISTS_BANK_ACCOUNT_IBAN = "112312312312312312";


    private static final UserPrincipal NOT_FOUND_USER_PRINCIPAL = UserPrincipal.builder()
            .id(ENCODED_USER_ID)
            .username(NOT_FOUND_USER)
            .build();
    private static final UserPrincipal ENCODED_PASSWORD_USER_PRINCIPAL = UserPrincipal.builder()
            .id(0)
            .username(ENCODED_PASSWORD_USER)
            .build();

    @Autowired
    private CreditCardService creditCardService;

    @MockBean
    private CreditCardPersistence creditCardPersistence;


    private final UserEntity userEntity = UserEntity.builder()
            .username(ENCODED_PASSWORD_USER)
            .build();

    @BeforeEach
    void setUp() {
        when(creditCardPersistence.findCreditCardsForUser(NOT_FOUND_USER_PRINCIPAL)).thenReturn(List.of());
        when(creditCardPersistence.findCreditCardsForUser(ENCODED_PASSWORD_USER_PRINCIPAL)).thenReturn(List.of(
            CreditCardEntity.builder()
                    .user(userEntity)
                    .build()
        ));
    }

    @Test
    void testFindCreditCardsForUserNameNotFoundUser() {
        assertTrue(creditCardService.findCreditCardsForUser(NOT_FOUND_USER_PRINCIPAL).isEmpty());
    }

    @Test
    void testFindCreditCardsForUser() {
        List<CreditCard> creditCards = creditCardService.findCreditCardsForUser(ENCODED_PASSWORD_USER_PRINCIPAL);
        assertFalse(creditCardService.findCreditCardsForUser(ENCODED_PASSWORD_USER_PRINCIPAL).isEmpty());
        assertEquals(1, creditCards.size());
    }

    @Test
    void testCreateCreditCardSuccess() {
        CreditCard creditCard = CreditCard.builder()
                .creditCardNumber(NEW_CREDIT_CARD_NUMBER)
                .build();
        when(creditCardPersistence.createCreditCard(any(), any())).thenReturn(CreditCardEntity.builder()
                .user(userEntity)
                .creditCardNumber(NEW_CREDIT_CARD_NUMBER)
                .build());
        CreditCard createdCreditCard = creditCardService.createCreditCard(ENCODED_PASSWORD_USER_PRINCIPAL, creditCard);
        assertEquals(NEW_CREDIT_CARD_NUMBER, createdCreditCard.getCreditCardNumber());
    }

    @Test
    void testCreateCreditCardUserNotFound() {
        when(creditCardPersistence.createCreditCard(any(), any())).thenThrow(new UserNotFoundException(ENCODED_PASSWORD_USER));
        CreditCard creditCard = CreditCard.builder()
                .creditCardNumber(NEW_CREDIT_CARD_NUMBER)
                .build();
        assertThrows(UserNotFoundException.class, () -> creditCardService.createCreditCard(NOT_FOUND_USER_PRINCIPAL, creditCard));
    }

    @Test
    void testCreateCreditCardAlreadyExists() {
        CreditCard creditCard = CreditCard.builder()
                .creditCardNumber(NEW_CREDIT_CARD_NUMBER)
                .build();
        when(creditCardPersistence.createCreditCard(any(), any())).thenThrow(new CreditCardAlreadyExistsException(ENCODED_PASSWORD_USER, NEW_CREDIT_CARD_NUMBER));
        assertThrows(CreditCardAlreadyExistsException.class, () -> creditCardService.createCreditCard(ENCODED_PASSWORD_USER_PRINCIPAL, creditCard));
    }

    @Test
    void testCreateCreditCardBankAccountNotFound() {
        CreditCard creditCard = CreditCard.builder()
                .creditCardNumber(NEW_CREDIT_CARD_NUMBER)
                .bankAccount(es.upm.mabills.model.BankAccount.builder()
                        .uuid(NOT_EXISTS_BANK_ACCOUNT_UUID)
                        .iban(NOT_EXISTS_BANK_ACCOUNT_IBAN)
                        .build())
                .build();
        when(creditCardPersistence.createCreditCard(any(), any())).thenThrow(new BankAccountNotFoundException("creditCardNumberWithoutBankAccount"));
        assertThrows(BankAccountNotFoundException.class, () -> creditCardService.createCreditCard(ENCODED_PASSWORD_USER_PRINCIPAL, creditCard));
    }
}
