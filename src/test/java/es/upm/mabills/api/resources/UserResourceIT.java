package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.api.dtos.TokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@ApiTestConfig
class UserResourceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String PASSWORD = "password";

    @Autowired
    private WebTestClient webTestClient;

    private LoginDto loginDto;

    @BeforeEach
    void beforeEach() {
        this.loginDto = LoginDto.builder()
                .username(ENCODED_PASSWORD_USER)
                .password(PASSWORD)
                .build();
    }

    @Test
    void testLoginSuccess() {
        this.webTestClient
            .post().uri(UserResource.USERS)
            .body(Mono.just(loginDto), LoginDto.class)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(TokenDto.class)
            .value(v -> {
                assertNotNull(v);
                assertNotNull(v.getToken());
            });
    }

    @Test
    void testLoginBadCredentials() {
        this.webTestClient
                .post().uri(UserResource.USERS)
                .body(Mono.just(createBadCredentialsLoginDto()), LoginDto.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    private LoginDto createBadCredentialsLoginDto() {
        return LoginDto.builder()
                .username(ENCODED_PASSWORD_USER)
                .password("badPassword")
                .build();
    }
}
