package es.upm.mabills.services;

import es.upm.mabills.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
class UserDetailsServiceImplTest {
    private static final String TEST_USERNAME = "username";
    private static final String TEST_PASSWORD = "password";
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Test
    void testLoadUserByUsername() {
        UserDetails user = this.userDetailsServiceImpl.loadUserByUsername(TEST_USERNAME);
        assertEquals(TEST_USERNAME, user.getUsername());
        assertEquals(TEST_PASSWORD, user.getPassword());
    }
}
