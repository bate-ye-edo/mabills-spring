package es.upm.mabills.api.resources;

import es.upm.mabills.api.Rest;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.api.dtos.RegisterDto;
import es.upm.mabills.api.dtos.TokenDto;
import es.upm.mabills.mappers.UserMapper;
import es.upm.mabills.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Rest
@RequestMapping(UserResource.USERS)
public class UserResource {
    public static final String REGISTER = "/register";
    public static final String USERS = "/users";
    public static final String LOGIN = "/login";
    public static final String REFRESH_TOKEN = "/refresh-token";

    private final UserService userService;
    private final UserMapper userMapper;
    private final String jwtHeader;

    @Autowired
    public UserResource(UserService userService, UserMapper userMapper, @Value("${jwt.header}") String jwtHeader) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtHeader = jwtHeader;
    }

    @PreAuthorize("permitAll()")
    @PostMapping(UserResource.LOGIN)
    public TokenDto loginUser(@Validated @RequestBody LoginDto loginDto) {
        return TokenDto.builder()
                .token(userService.login(loginDto.getUsername(), loginDto.getPassword()))
                .build();
    }

    @PreAuthorize("permitAll()")
    @PostMapping(UserResource.REGISTER)
    public TokenDto registerUser(@Validated @RequestBody RegisterDto registerDto) {
        return TokenDto.builder()
                .token(userService.register(userMapper.toUser(registerDto)))
                .build();
    }

    @GetMapping(UserResource.REFRESH_TOKEN)
    public TokenDto refreshUserToken(HttpServletRequest request) {
        return TokenDto.builder()
                .token(userService.refreshToken(
                    request.getHeader(jwtHeader)
                ))
                .build();
    }
}
