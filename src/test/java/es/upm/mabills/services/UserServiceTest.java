package es.upm.mabills.services;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.exceptions.UserAlreadyExistsException;
import es.upm.mabills.model.User;
import es.upm.mabills.persistence.UserPersistence;
import es.upm.mabills.persistence.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTestConfig
class UserServiceTest {
    private static final String TEST_USERNAME = "username";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_TOKEN = "token";
    private static final String TEST_NOT_EXISTS_USERNAME = "NotExistsUsername";

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserPersistence userPersistence;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private TokenCacheService tokenCacheService;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(userPersistence.findUserByUsername(anyString())).thenReturn(UserEntity.builder()
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .build());
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.createToken(anyString())).thenReturn("token");
        doNothing().when(tokenCacheService).blackListToken(anyString());
    }

    @Test
    void testLoginSuccess() {
        String token = userService.login(TEST_USERNAME, TEST_PASSWORD);
        assertEquals(TEST_TOKEN, token);
    }

    @Test
    void testLoginFailurePasswordIncorrect() {
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        assertThrows(BadCredentialsException.class, () -> userService.login(TEST_USERNAME, TEST_PASSWORD));
    }

    @Test
    void testLoginFailureUserNotExists() {
        when(userPersistence.findUserByUsername(anyString())).thenReturn(null);
        assertThrows(BadCredentialsException.class, () -> userService.login(TEST_NOT_EXISTS_USERNAME, TEST_PASSWORD));
    }

    @Test
    void testRegisterSuccess() {
        when(passwordEncoder.encode(anyString())).thenReturn(TEST_PASSWORD);
        User user = buildNewRegisterUser();
        when(userPersistence.registerUser(user, TEST_PASSWORD)).thenReturn(new UserEntity(user, TEST_PASSWORD));
        String token = userService.register(user);
        assertEquals(TEST_TOKEN, token);
    }

    @Test
    void testRegisterFailureUserAlreadyExists() {
        when(userPersistence.registerUser(any(), anyString())).thenThrow(new UserAlreadyExistsException(""));
        assertThrows(UserAlreadyExistsException.class, () -> userService.register(buildNewRegisterUser()));
    }

    @Test
    void testRefreshTokenSuccess() {
        when(jwtService.extractToken(anyString())).thenReturn(TEST_TOKEN);
        when(jwtService.username(anyString())).thenReturn(TEST_USERNAME);
        assertEquals(TEST_TOKEN, userService.refreshToken(TEST_TOKEN));
    }

    @Test
    void testLogoutSuccess() {
        when(jwtService.extractToken(anyString())).thenReturn(TEST_TOKEN);
        userService.logout(TEST_TOKEN);
        verify(tokenCacheService).blackListToken(TEST_TOKEN);
    }

    private User buildNewRegisterUser() {
        return User.builder()
                .username("newRegisterUser")
                .password("newRegisterUserPassword")
                .email("newRegisterUserEmail")
                .mobile("6666666661")
                .build();
    }
}
