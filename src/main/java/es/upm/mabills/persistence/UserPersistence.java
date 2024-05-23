package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.DuplicatedEmailException;
import es.upm.mabills.exceptions.UserAlreadyExistsException;
import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.model.User;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.UserRepository;
import io.vavr.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;



@Repository
public class UserPersistence {
    private static final Logger LOGGER = LogManager.getLogger(UserPersistence.class);
    private final UserRepository userRepository;

    @Autowired
    public UserPersistence(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserEntity registerUser(User user, String encodedPassword) throws UserAlreadyExistsException, DuplicatedEmailException {
        assertUserNotExists(user);
        return userRepository.save(new UserEntity(user, encodedPassword));
    }

    private void assertUserNotExists(User user) {
        if (findUserByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistsException(user.getUsername());
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new DuplicatedEmailException(user.getEmail());
        }
    }

    public UserEntity updateUser(String username, User user) throws UserNotFoundException {
        return Try.of(()->userRepository.findByUsername(username))
                .map(userEntity ->{
                    userEntity.updateUserEntity(user);
                    LOGGER.info("User {} updated", userEntity.getUsername());
                    return userEntity;
                })
                .onFailure(NullPointerException.class, e -> {
                    throw new UserNotFoundException(username);
                })
                .andThenTry(userRepository::save)
                .getOrElseThrow(()->new DuplicatedEmailException(user.getEmail()));
    }
}
