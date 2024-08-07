package es.upm.mabills.services;

import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.mappers.UserMapper;
import es.upm.mabills.model.User;
import es.upm.mabills.persistence.UserPersistence;
import io.vavr.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static io.micrometer.common.util.StringUtils.isBlank;

@Service
public class UserService {
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);
    private static final String INVALID_CREDENTIALS = "Invalid credentials";

    private final UserPersistence userPersistence;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenCacheService tokenCacheService;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserPersistence userPersistence, JwtService jwtService,
                       PasswordEncoder passwordEncoder, TokenCacheService tokenCacheService,
                       UserMapper userMapper) {
        this.userPersistence = userPersistence;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.tokenCacheService = tokenCacheService;
        this.userMapper = userMapper;
    }

    public String login(String username, String password) {
        return jwtService.createToken(
            findUserAndReturnUsername(username, password)
        );
    }

    private String findUserAndReturnUsername(String username, String password) {
        return Try.of(() -> checkPasswordAndReturnUser(username, password))
            .map(User::getUsername)
            .get();
    }

    private User checkPasswordAndReturnUser(String username, String password) {
        return Try.of(() -> userPersistence.findUserByUsername(username))
            .filter(user -> passwordEncoder.matches(password, user.getPassword()))
            .map(userMapper::toUser)
            .onFailure(e -> LOGGER.error("Exception on login: ",e))
            .getOrElseThrow(() -> new BadCredentialsException(INVALID_CREDENTIALS));
    }

    public String register(User user) {
        return jwtService.createToken(userPersistence
                    .registerUser(user, encodePassword(user.getPassword()))
                    .getUsername());
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public String refreshToken(String token) {
        String extractedToken = jwtService.extractToken(token);
        tokenCacheService.blackListToken(extractedToken);
        return jwtService.createToken(jwtService.username(extractedToken));
    }

    public void logout(String header) {
        tokenCacheService.blackListToken(jwtService.extractToken(header));
    }

    @Transactional
    public User getUserByUsername(String username) {
        return userMapper.toUserProfile(
                Optional.ofNullable(userPersistence.findUserByUsername(username))
                    .orElseThrow(() -> new UserNotFoundException(username))
        );
    }

    public User updateUser(String username, User user) {
        if(!isBlank(user.getPassword())) {
            user.setPassword(encodePassword(user.getPassword()));
        }
        return userMapper.toUser(
            userPersistence.updateUser(username, user)
        );
    }
}
