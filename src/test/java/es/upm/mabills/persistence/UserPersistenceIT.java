package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import es.upm.mabills.exceptions.UserAlreadyExistsException;
import es.upm.mabills.model.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class UserPersistenceIT {
    private static final String NEW_REGISTER_ENCODED_PASSWORD = "newRegisterUserEncodedPassword";
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
    void testFindUserIdByUsername() {
        assertTrue(userPersistence.findUserIdByUsername("username") > 0);
    }

    @Test
    void testFindUserByUsernameNotFound() {
        assertNull(userPersistence.findUserByUsername("notFound"));
    }

    @Test
    void testRegisterUserSuccess() {
        User newUser = userPersistence.registerUser(buildNewRegisterUser(), NEW_REGISTER_ENCODED_PASSWORD);
        assertNotNull(newUser);
        assertEquals("newRegisterUser", newUser.getUsername());
        assertEquals("newRegisterUserEncodedPassword", newUser.getPassword());
        assertEquals("newRegisterUserEmail", newUser.getEmail());
        assertEquals("6666666661", newUser.getMobile());
    }

    @Test
    void testRegisterUserUsernameAlreadyExists() {
        assertThrows(UserAlreadyExistsException.class, () -> userPersistence.registerUser(buildUserUsernameAlreadyExists(), NEW_REGISTER_ENCODED_PASSWORD));
    }

    @Test
    void testRegisterUserEmailAlreadyExists() {
        assertThrows(DataIntegrityViolationException.class, () -> userPersistence.registerUser(buildUserEmailAlreadyExists(), NEW_REGISTER_ENCODED_PASSWORD));
    }

    private User buildUserUsernameAlreadyExists() {
        return User.builder()
                .username("username")
                .password("otherPassword")
                .email("otherEmail")
                .mobile("666666666")
                .build();
    }

    private User buildUserEmailAlreadyExists() {
        return User.builder()
                .username("us")
                .password("otherPassword")
                .email("email")
                .mobile("666666666")
                .build();
    }

    private User buildNewRegisterUser() {
        return User.builder()
                .username("newRegisterUser")
                .password("newRegisterUserPassWord")
                .email("newRegisterUserEmail")
                .mobile("6666666661")
                .build();
    }
}
