package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.UserAlreadyExistsException;
import es.upm.mabills.model.User;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserPersistence {
    private final UserRepository userRepository;

    @Autowired
    public UserPersistence(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserEntity registerUser(User user, String encodedPassword) {
        assertUserNotExists(user);
        return userRepository.save(new UserEntity(user, encodedPassword));
    }

    private void assertUserNotExists(User user) {
        if (findUserByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistsException(user.getUsername());
        }
    }
}
