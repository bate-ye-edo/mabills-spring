package es.upm.mabills.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {
    private static final String BEARER = "Bearer ";
    private static final int PARTIES = 3;
    private static final String USER_CLAIM = "user";
    private static final String ROLE_CLAIM = "role";

    private final String secret;
    private final String issuer;
    private final int expire;
    public static final String ROLE_USER = "ROLE_USER";
    @Autowired
    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.issuer}") String issuer,
                      @Value("${jwt.expiration}") int expire) {
        this.secret = secret;
        this.issuer = issuer;
        this.expire = expire;
    }

    public String extractToken(String bearer) {
        if (bearer != null && bearer.startsWith(BEARER) && PARTIES == bearer.split("\\.").length) {
            return bearer.substring(BEARER.length());
        }
        return "";
    }

    public String createToken(String username) {
        return JWT.create()
                .withIssuer(this.issuer)
                .withIssuedAt(new Date())
                .withNotBefore(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + this.expire * 1000L))
                .withClaim(USER_CLAIM, username)
                .withClaim(ROLE_CLAIM, ROLE_USER)
                .sign(Algorithm.HMAC256(this.secret));
    }

    public boolean isValidToken(String token) {
        return this.validateTokenExists(token) && this.validateDecodedTokenExpirationDate(token);
    }

    public boolean isTokenExpired(String token) {
        return !this.validateDecodedTokenExpirationDate(token);
    }

    private boolean validateTokenExists(String token) {
        return this.verify(token).isPresent();
    }

    private boolean validateDecodedTokenExpirationDate(String token) {
        return this.verify(token)
                .map(jwt -> jwt.getExpiresAt().after(new Date()))
                .orElse(false);
    }

    public String username(String authorization) {
        return this.verify(authorization)
                .map(jwt -> jwt.getClaim(USER_CLAIM).asString())
                .orElse("");
    }

    private Optional<DecodedJWT> verify(String token) {
        try {
            return Optional.of(
                    JWT.require(Algorithm.HMAC256(this.secret))
                        .withIssuer(this.issuer).build()
                        .verify(token)
            );
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

}
