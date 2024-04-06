package es.upm.mabills.services;

import es.upm.mabills.UnitTestConfig;
import es.upm.mabills.model.User;
import es.upm.mabills.persistence.UserPersistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@UnitTestConfig
class UserServiceTest {
    private static final String TEST_USERNAME = "username";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_TOKEN = "token";
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserPersistence userPersistence;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(userPersistence.findUserByUsername(anyString())).thenReturn(User.builder()
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .build());
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.createToken(anyString())).thenReturn("token");
    }

    @Test
    void testLoginSuccess() {
        String token = userService.login(TEST_USERNAME, TEST_PASSWORD);
        assertEquals(TEST_TOKEN, token);
    }

    @Test
    void testLoginFailure() {
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        assertThrows(BadCredentialsException.class, () -> userService.login(TEST_USERNAME, TEST_PASSWORD));
    }
}
