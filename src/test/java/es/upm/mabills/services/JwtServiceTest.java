package es.upm.mabills.services;

import es.upm.mabills.UnitTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@UnitTestConfig
class JwtServiceTest {
    private static final String USERNAME = "username";
    private static final String EMPTY_TOKEN = "";
    private static final String TEST_SECRET = "TEST_SECRET";
    private static final String TEST_ISSUER = "TEST_ISSUER";
    private static final int TEST_EXPIRATION = 0;

    @Autowired
    private JwtService jwtService;
    private String token;

    @BeforeEach
    void setUp() {
        this.token = jwtService.createToken(USERNAME);
    }

    @Test
    void testCreateToken() {
        assertNotNull(this.token);
    }

    @Test
    void testExtractToken() {
        String bearerToken = "Bearer " + this.token;
        assertEquals("", jwtService.extractToken(this.token));
        assertEquals(this.token, jwtService.extractToken(bearerToken));
    }

    @Test
    void testIsValidTokenTokenEmptyThenFalse() {
        assertFalse(jwtService.isValidToken(EMPTY_TOKEN));
    }

    @Test
    void testIsValidTokenTokenCorrectThenTrue() {
        assertTrue(jwtService.isValidToken(this.token));
    }

    @Test
    void testIsValidTokenTokenExpiredThenFalse() {
        JwtService tokenService = new JwtService(TEST_SECRET, TEST_ISSUER, TEST_EXPIRATION);
        String expiredToken = tokenService.createToken(USERNAME);
        assertFalse(tokenService.isValidToken(expiredToken));
    }

    @Test
    void testIsExpiredToken() {
        JwtService tokenService = new JwtService(TEST_SECRET, TEST_ISSUER, TEST_EXPIRATION);
        String expiredToken = tokenService.createToken(USERNAME);
        assertTrue(tokenService.isTokenExpired(expiredToken));
    }

    @Test
    void testExtractUserNameFromTokenWhenTokenEmpty() {
        assertEquals("", jwtService.username(EMPTY_TOKEN));
    }

    @Test
    void testExtractUserNameFromTokenWhenTokenCorrect() {
        assertEquals(USERNAME, jwtService.username(this.token));
    }
}
