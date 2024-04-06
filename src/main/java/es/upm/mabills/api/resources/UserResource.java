package es.upm.mabills.api.resources;

import es.upm.mabills.api.Rest;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.api.dtos.TokenDto;
import es.upm.mabills.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Rest
@RequestMapping(UserResource.USERS)
public class UserResource {
    public static final String USERS = "/users";

    private final UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("permitAll()")
    public TokenDto login(@Validated @RequestBody LoginDto loginDto) {
        return TokenDto.builder()
                .token(userService.login(loginDto.getUsername(), loginDto.getPassword()))
                .build();
    }
}
