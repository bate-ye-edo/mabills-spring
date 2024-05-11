package es.upm.mabills.services;

import es.upm.mabills.exceptions.UserAlreadyExistsException;
import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.mappers.UserMapper;
import es.upm.mabills.model.User;
import es.upm.mabills.persistence.UserPersistence;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
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
            .getOrElseThrow(() -> new BadCredentialsException(INVALID_CREDENTIALS));
    }

    public String register(User user) {
        return Try.of(()->jwtService.createToken(userPersistence
                            .registerUser(user, encodePassword(user.getPassword()))
                            .getUsername()))
                .getOrElseThrow(() -> new UserAlreadyExistsException(user.getUsername()));
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

    public User getUserByUsername(String username) {
        return userMapper.toUser(
                Optional.ofNullable(userPersistence.findUserByUsername(username))
                    .orElseThrow(() -> new UserNotFoundException(username))
        );
    }
}
