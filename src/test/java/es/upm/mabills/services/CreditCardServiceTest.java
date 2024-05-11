package es.upm.mabills.services;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.model.CreditCard;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@UnitTestConfig
class CreditCardServiceTest {
    private static final String NOT_FOUND_USER = "notFoundUser";
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    @Autowired
    private CreditCardService creditCardService;

    @MockBean
    private CreditCardPersistence creditCardPersistence;

    private final UserEntity userEntity = UserEntity.builder()
            .username(ENCODED_PASSWORD_USER)
            .build();

    @BeforeEach
    void setUp() {
        when(creditCardPersistence.findCreditCardsByUserName(NOT_FOUND_USER)).thenReturn(List.of());
        when(creditCardPersistence.findCreditCardsByUserName(ENCODED_PASSWORD_USER)).thenReturn(List.of(
            CreditCardEntity.builder()
                    .user(userEntity)
                    .build()
        ));
    }

    @Test
    void testFindCreditCardsByUserNameNotFoundUser() {
        assertTrue(creditCardService.findCreditCardsByUserName(NOT_FOUND_USER).isEmpty());
    }

    @Test
    void testFindCreditCardsByUserName() {
        List<CreditCard> creditCards = creditCardService.findCreditCardsByUserName(ENCODED_PASSWORD_USER);
        assertFalse(creditCardService.findCreditCardsByUserName(ENCODED_PASSWORD_USER).isEmpty());
        assertEquals(1, creditCards.size());
        assertEquals(ENCODED_PASSWORD_USER, creditCards.get(0).getUser().getUsername());
    }

}
