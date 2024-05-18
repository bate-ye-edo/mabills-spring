package es.upm.mabills.api.resources;

import es.upm.mabills.api.ApiTestConfig;
import es.upm.mabills.api.RestClientTestService;
import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.api.dtos.RegisterDto;
import es.upm.mabills.api.dtos.TokenDto;
import es.upm.mabills.api.http_errors.ErrorMessage;
import es.upm.mabills.exceptions.UserAlreadyExistsException;
import es.upm.mabills.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.MethodArgumentNotValidException;
import reactor.core.publisher.Mono;

import static es.upm.mabills.TestStringUtils.assertNotBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@ApiTestConfig
class UserResourceIT {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String LOGOUT_USER = "logOutUser";
    private static final String PASSWORD = "password";
    private static final String GOOD_PASSWORD = "passworD1.";
    private static final String TO_UPDATE_USER = "toUpdateUser";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

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
            .post().uri(UserResource.USERS+UserResource.LOGIN)
            .body(Mono.just(loginDto), LoginDto.class)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(TokenDto.class)
            .value(v -> {
                assertNotNull(v);
                assertNotNull(v.token());
            });
    }

    @Test
    void testLoginBadCredentials() {
        this.webTestClient
                .post().uri(UserResource.USERS+UserResource.LOGIN)
                .body(Mono.just(createBadCredentialsLoginDto()), LoginDto.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void testRegisterUserSuccess() {
        this.webTestClient
                .post().uri(UserResource.USERS+UserResource.REGISTER)
                .body(Mono.just(createRegisterUserDto()), RegisterDto.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(TokenDto.class)
                .value(v -> {
                    assertNotNull(v);
                    assertNotNull(v.token());
                });
    }

    @Test
    void testRegisterUserAlreadyExists() {
        this.webTestClient
                .post().uri(UserResource.USERS+UserResource.REGISTER)
                .body(Mono.just(createAlreadyExistsUser()), RegisterDto.class)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatusCode.valueOf(409))
                .expectBody(ErrorMessage.class)
                .value(v -> {
                    assertNotNull(v);
                    assertNotNull(v.getError());
                    assertEquals(UserAlreadyExistsException.class.getSimpleName(), v.getError());
                });
    }

    @Test
    void testRegisterUserEmailBadFormat() {
        this.webTestClient
                .post().uri(UserResource.USERS+UserResource.REGISTER)
                .body(Mono.just(createBadEmailRegisterUser()), RegisterDto.class)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ErrorMessage.class)
                .value(v -> {
                    assertNotNull(v);
                    assertEquals(MethodArgumentNotValidException.class.getSimpleName(), v.getError());
                    assertNotNull(v.getErrorFieldNames());
                    assertFalse(v.getErrorFieldNames().isEmpty());
                    assertEquals("email", v.getErrorFieldNames().get(0));
                });
    }


    @Test
    void testRegisterUserMobileBadFormat() {
        this.webTestClient
                .post().uri(UserResource.USERS+UserResource.REGISTER)
                .body(Mono.just(createBadMobileRegisterUser()), RegisterDto.class)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ErrorMessage.class)
                .value(v -> {
                    assertNotNull(v);
                    assertEquals(MethodArgumentNotValidException.class.getSimpleName(), v.getError());
                    assertNotNull(v.getErrorFieldNames());
                    assertFalse(v.getErrorFieldNames().isEmpty());
                    assertEquals("mobile", v.getErrorFieldNames().get(0));
                });
    }

    @Test
    void testRefreshTokenSuccess(){
        WebTestClient client = restClientTestService.loginDefault(this.webTestClient);
        String oldToken = restClientTestService.extractTokenFromHeaders();
        assertRefreshSuccess(client, oldToken);
    }

    @Test
    void testRefreshTokenBlackListed() {
        WebTestClient client = restClientTestService.login(this.webTestClient, getOtherLoginDto());
        String oldToken = restClientTestService.extractTokenFromHeaders();
        assertRefreshSuccess(client, oldToken);
        assertRefreshFailed(client);
    }

    @Test
    void testRefreshTokenFailedNotLogged() {
        assertRefreshFailed(this.webTestClient);
    }

    @Test
    void testLogoutSuccess() {
        WebTestClient client = restClientTestService.login(this.webTestClient, getLogoutDto());
        client
            .post().uri(UserResource.USERS+UserResource.LOGOUT)
            .exchange()
            .expectStatus()
            .isOk();
        client.post().uri(UserResource.USERS+UserResource.REFRESH_TOKEN)
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

    @Test
    void testGetUserProfileSuccess() {
        restClientTestService.loginDefault(this.webTestClient)
            .get().uri(UserResource.USERS)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(User.class)
            .value(user -> {
                assertNotNull(user);
                assertNotBlank(user.getUsername());
                assertNotBlank(user.getEmail());
                assertNotBlank(user.getMobile());
                assertNull(user.getPassword());
            });
    }

    @Test
    void testGetUserProfileFailedNotLogged() {
        this.webTestClient
            .get().uri(UserResource.USERS)
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

    @Test
    void testUpdateUser() {
        User updatedUser = getNewToUpdateUserSuccess();
        updateUserLogin()
                .body(Mono.just(updatedUser), User.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(User.class)
                .value(user -> {
                    assertNotNull(user);
                    assertEquals(TO_UPDATE_USER, user.getUsername());
                    assertEquals(updatedUser.getEmail(), user.getEmail());
                    assertEquals(updatedUser.getMobile(), user.getMobile());
                });
    }

    @Test
    void testUpdateUserFailedNotLogged() {
        this.webTestClient
            .put().uri(UserResource.USERS)
            .body(Mono.just(getNewToUpdateUserSuccess()), User.class)
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

    @Test
    void testUpdateUserEmailFailed() {
        updateUserLogin()
            .body(Mono.just(getNewToUpdateUserEmailFails()), User.class)
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    void testUpdateUserMobileFailed() {
        updateUserLogin()
            .body(Mono.just(getNewToUpdateUserMobileFails()), User.class)
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    void testUpdateUserPasswordFailed() {
        updateUserLogin()
            .body(Mono.just(getNewToUpdateUserPasswordFails()), User.class)
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    private WebTestClient.RequestBodySpec updateUserLogin() {
        return restClientTestService.login(this.webTestClient, getToUpdateLoginDto())
                .put().uri(UserResource.USERS);
    }

    private User getNewToUpdateUserEmailFails() {
        return User.builder()
                .username(TO_UPDATE_USER)
                .email("email")
                .mobile("1231231231231232132")
                .build();
    }

    private User getNewToUpdateUserMobileFails() {
        return User.builder()
                .username(TO_UPDATE_USER)
                .email("email@email")
                .mobile("asdf")
                .build();
    }

    private User getNewToUpdateUserPasswordFails() {
        return User.builder()
                .username(TO_UPDATE_USER)
                .email("email@email")
                .mobile("1231231231231232132")
                .password("a")
                .build();
    }

    private User getNewToUpdateUserSuccess() {
        return User.builder()
                .username(TO_UPDATE_USER)
                .email("toUpdateUserEmail@hola.com")
                .mobile("1231231231231232132")
                .build();
    }

    private LoginDto getToUpdateLoginDto() {
        return LoginDto.builder()
                .username(TO_UPDATE_USER)
                .password(PASSWORD)
                .build();
    }

    private LoginDto getLogoutDto() {
        return LoginDto.builder()
                .username(LOGOUT_USER)
                .password(PASSWORD)
                .build();
    }

    private LoginDto getOtherLoginDto() {
        return LoginDto.builder()
                .username("otherUser")
                .password("password")
                .build();
    }

    private void assertRefreshFailed(WebTestClient client) {
        client
            .post().uri(UserResource.USERS+UserResource.REFRESH_TOKEN)
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

    private void assertRefreshSuccess(WebTestClient webTestClient, String oldToken) {
        webTestClient
            .post().uri(UserResource.USERS+UserResource.REFRESH_TOKEN)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(TokenDto.class)
            .value(tokenDto -> {
                assertNotNull(tokenDto);
                assertNotEquals(oldToken, tokenDto.token());
            });
    }

    private RegisterDto createBadMobileRegisterUser() {
        return RegisterDto.builder()
                .username("badMobileRegisterUser")
                .password(GOOD_PASSWORD)
                .email("email@email")
                .mobile("6666666661a")
                .build();
    }

    private RegisterDto createBadEmailRegisterUser() {
        return RegisterDto.builder()
                .username("badEmailRegisterUser")
                .password(GOOD_PASSWORD)
                .email("email")
                .mobile("6666666661")
                .build();
    }

    private RegisterDto createAlreadyExistsUser() {
        return RegisterDto.builder()
                .username(ENCODED_PASSWORD_USER)
                .password(GOOD_PASSWORD)
                .email("email@email")
                .mobile("666666666")
                .build();
    }

    private RegisterDto createRegisterUserDto() {
        return RegisterDto.builder()
                .username("newRegisterUser")
                .password(GOOD_PASSWORD)
                .email("newRegisterUserEmail@email.es")
                .mobile("6666666661")
                .build();
    }

    private LoginDto createBadCredentialsLoginDto() {
        return LoginDto.builder()
                .username(ENCODED_PASSWORD_USER)
                .password("badPassword")
                .build();
    }
}
