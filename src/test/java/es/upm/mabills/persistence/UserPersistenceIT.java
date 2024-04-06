package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.model.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class UserPersistenceIT {
    @Autowired
    private UserPersistence userPersistence;

    @Test
    @SneakyThrows
    void testFindUserByUsernameAndPassword() {
        User user = this.userPersistence.login("username", "password");
        assertNotNull(user);
        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("email", user.getEmail());
        assertEquals("666666666", user.getMobile());
    }

    @Test
    void testFindUserByUsernameAndPasswordNotFoundException() {
        assertThrows(UserNotFoundException.class, ()->this.userPersistence.login("username", "passwo"));
    }

    @Test
    @SneakyThrows
    void testFindUserByUsername() {
        User user = this.userPersistence.findUserByUsername("username");
        assertNotNull(user);
        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("email", user.getEmail());
        assertEquals("666666666", user.getMobile());
    }

    @Test
    void testFindUserByUsernameNotFoundException() {
        assertThrows(UserNotFoundException.class, ()->this.userPersistence.findUserByUsername("usernam"));
    }
}
