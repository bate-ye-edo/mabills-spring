package es.upm.mabills.persistence;

import es.upm.mabills.TestConfig;
import es.upm.mabills.exceptions.DuplicatedEmailException;
import es.upm.mabills.exceptions.UserAlreadyExistsException;
import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.model.User;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.BankAccountRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class UserPersistenceIT {
    private static final String NEW_REGISTER_ENCODED_PASSWORD = "newRegisterUserEncodedPassword";
    private static final String NEW_REGISTER_USER = UUID.randomUUID().toString();
    private static final String TO_UPDATE_USER = "toUpdateUser";
    private static final String NOT_FOUND_USER = "notFoundUser";
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";

    @Autowired
    private UserPersistence userPersistence;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Test
    @SneakyThrows
    void testFindUserByUsername() {
        UserEntity user = this.userPersistence.findUserByUsername("username");
        assertNotNull(user);
        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("email", user.getEmail());
        assertEquals("666666666", user.getMobile());
    }

    @Test
    void testFindUserByUsernameNotFound() {
        assertNull(userPersistence.findUserByUsername(NOT_FOUND_USER));
    }

    @Test
    void testRegisterUserSuccess() {
        UserEntity newUser = userPersistence.registerUser(buildNewRegisterUser(), NEW_REGISTER_ENCODED_PASSWORD);
        assertNotNull(newUser);
        assertEquals(NEW_REGISTER_USER, newUser.getUsername());
        assertEquals("newRegisterUserEncodedPassword", newUser.getPassword());
        assertEquals("newRegisterUserEmail", newUser.getEmail());
        assertEquals("6666666661", newUser.getMobile());
    }

    @Test
    void testRegisterUserUsernameAlreadyExists() {
        User user = buildUserUsernameAlreadyExists();
        assertThrows(UserAlreadyExistsException.class, () -> userPersistence.registerUser(user, NEW_REGISTER_ENCODED_PASSWORD));
    }

    @Test
    void testRegisterUserEmailAlreadyExists() {
        User user = buildUserEmailAlreadyExists();
        assertThrows(DuplicatedEmailException.class, () -> userPersistence.registerUser(user, NEW_REGISTER_ENCODED_PASSWORD));
    }

    @Test
    void testUpdateUserSuccess() {
        User user = buildUpdateUserNewUser();
        UserEntity updatedUser = userPersistence.updateUser(TO_UPDATE_USER, user);
        assertNotNull(updatedUser);
        assertEquals(user.getUsername(), updatedUser.getUsername());
        assertEquals(user.getPassword(), updatedUser.getPassword());
        assertEquals(user.getEmail(), updatedUser.getEmail());
        assertEquals(user.getMobile(), updatedUser.getMobile());
    }

    @Test
    void testUpdateUserNotFound() {
        User user = buildUpdateUserNewUser();
        assertThrows(UserNotFoundException.class, () -> userPersistence.updateUser(NOT_FOUND_USER, user));
    }

    @Test
    void testUpdateUserEmailAlreadyExists() {
        User user = buildUserEmailAlreadyExists();
        assertThrows(DuplicatedEmailException.class, () -> userPersistence.updateUser(TO_UPDATE_USER, user));
    }

    private User buildUpdateUserNewUser() {
        return User.builder()
                .username(TO_UPDATE_USER)
                .password("newToUpdateUserEmail")
                .email("newToUpdateUserEmail")
                .mobile("12301239412312")
                .build();
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
                .username(NEW_REGISTER_USER)
                .password("newRegisterUserPassWord")
                .email("newRegisterUserEmail")
                .mobile("6666666661")
                .build();
    }
}
