package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
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
    void testFindUserByUsername() {
        User user = this.userPersistence.findUserByUsername("username");
        assertNotNull(user);
        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("email", user.getEmail());
        assertEquals("666666666", user.getMobile());
    }

    @Test
    void testFindUserByUsernameNotFound() {
        assertNull(userPersistence.findUserByUsername("notFound"));
    }
}
