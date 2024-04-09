package es.upm.mabills.services;

import es.upm.mabills.UnitTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@UnitTestConfig
class TokenCacheServiceTest {
    private static final String BLACK_LISTED_TOKEN = "BLACK_LISTED_TOKEN";
    private static final String NOT_BLACK_LISTED_TOKEN = "NOT_BLACK_LISTED_TOKEN";

    @MockBean
    private JwtService jwtService;

    @Autowired
    private TokenCacheService tokenCacheService;

    @Test
    void testIsTokenBlackListedTokenNotBlackListedThenFalse() {
        assertFalse(tokenCacheService.isTokenBlackListed(NOT_BLACK_LISTED_TOKEN));
    }

    @Test
    void testIsTokenBlackListedTokenBlackListedThenTrue() {
        tokenCacheService.blackListToken(BLACK_LISTED_TOKEN);
        assertTrue(tokenCacheService.isTokenBlackListed(BLACK_LISTED_TOKEN));
    }

    @Test
    void testCleanTokenCacheTokenBlackListedThenRemoved() {
        when(jwtService.isTokenExpired(anyString())).thenReturn(true);
        tokenCacheService.blackListToken(BLACK_LISTED_TOKEN);
        tokenCacheService.cleanTokenCache();
        assertFalse(tokenCacheService.isTokenBlackListed(BLACK_LISTED_TOKEN));
    }
}
