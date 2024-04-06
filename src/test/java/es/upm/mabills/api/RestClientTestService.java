package es.upm.mabills.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.test.web.reactive.server.WebTestClient;

@Service
public class RestClientTestService {
    private final String tokenPrefix;
    private final String authorizationHeader;
    @Autowired
    public RestClientTestService(@Value("${jwt.prefix}") String tokenPrefix, @Value("${jwt.header}") String authorizationHeader) {
        this.tokenPrefix = tokenPrefix;
        this.authorizationHeader = authorizationHeader;
    }
    public WebTestClient login(WebTestClient webTestClient) {
        // TODO: call the login endpoint to get a token
        return webTestClient.mutate()
                .defaultHeader(this.authorizationHeader, this.tokenPrefix + "token")
                .build();
    }
}
