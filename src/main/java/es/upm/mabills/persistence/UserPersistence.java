package es.upm.mabills.persistence;

import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.model.User;
import es.upm.mabills.persistence.mappers.UserMapper;
import es.upm.mabills.persistence.repositories.UserRepository;
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
    public User login(String username, String password) throws UserNotFoundException {
        return userMapper.toUser(
            userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(UserNotFoundException::new)
        );
    }
    public User findUserByUsername(String username) {
        return userMapper.toUser(
            userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new)
        );
    }
}
