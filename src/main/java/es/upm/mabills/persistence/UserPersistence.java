package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.UserAlreadyExistsException;
import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.model.User;
import es.upm.mabills.mappers.UserMapper;
import es.upm.mabills.persistence.entities.UserEntity;
import es.upm.mabills.persistence.repositories.UserRepository;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserPersistence {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserPersistence(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public User findUserByUsername(String username) {
        return userMapper.toUser(userRepository.findByUsername(username));
    }

    public int findUserIdByUsername(String username) {
        return Try.of(() -> userRepository.findByUsername(username).getId())
                .getOrElseThrow(() -> new UserNotFoundException(username));
    }

    public User registerUser(User user, String encodedPassword) {
        assertUserNotExists(user);
        return userMapper.toUser(userRepository.save(new UserEntity(user, encodedPassword)));
    }

    private void assertUserNotExists(User user) {
        if (findUserByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistsException(user.getUsername());
        }
    }
}
