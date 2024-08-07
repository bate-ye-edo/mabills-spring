package es.upm.mabills.api;

import es.upm.mabills.api.dtos.LoginDto;
import es.upm.mabills.api.dtos.TokenDto;
import es.upm.mabills.api.resources.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Service
@Profile("test")
public class RestClientTestService {
    private static final String ENCODED_PASSWORD_USER = "encodedPasswordUser";
    private static final String PASSWORD = "password";
    private final String tokenPrefix;
    private final String authorizationHeader;
    private final LoginDto loginDto;
    private final HttpHeaders headers;

    @Autowired
    public RestClientTestService(@Value("${jwt.prefix}") String tokenPrefix, @Value("${jwt.header}") String authorizationHeader) {
        this.tokenPrefix = tokenPrefix;
        this.authorizationHeader = authorizationHeader;
        this.loginDto = LoginDto.builder()
                .username(ENCODED_PASSWORD_USER)
                .password(PASSWORD)
                .build();
        headers = new HttpHeaders();
    }

    public WebTestClient loginDefault(WebTestClient webTestClient) {
        return getWebTestClientWithToken(webTestClient,
                getToken(webTestClient, null));
    }

    public WebTestClient login(WebTestClient webTestClient, LoginDto login) {
        return getWebTestClientWithToken(webTestClient,
                getToken(webTestClient, login));
    }

    private WebTestClient getWebTestClientWithToken(WebTestClient webTestClient, TokenDto tokenDto) {
        assertNotNull(tokenDto);
        clearHeaders();
        addTokenToHeaders(tokenDto.token());
        return webTestClient.mutate()
                .defaultHeader(this.authorizationHeader, this.tokenPrefix + tokenDto.token())
                .build();
    }

    private TokenDto getToken(WebTestClient webTestClient, LoginDto login) {
        return webTestClient
                .post().uri(UserResource.USERS+UserResource.LOGIN)
                .body(Mono.just(getDefaultLoginDtoIfNull(login)), LoginDto.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(TokenDto.class)
                .returnResult()
                .getResponseBody();
    }

    private LoginDto getDefaultLoginDtoIfNull(LoginDto login) {
        return login == null ? this.loginDto : login;
    }

    private void addTokenToHeaders(String token) {
        headers.add(this.authorizationHeader, this.tokenPrefix+token);
    }

    private void clearHeaders() {
        headers.clear();
    }

    public String extractTokenFromHeaders() {
        return Objects.requireNonNull(headers.get(this.authorizationHeader)).get(0);
    }
}
