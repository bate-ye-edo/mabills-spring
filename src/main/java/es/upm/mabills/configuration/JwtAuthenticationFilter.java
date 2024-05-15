package es.upm.mabills.configuration;

import es.upm.mabills.exceptions.UserNotFoundException;
import es.upm.mabills.model.UserPrincipal;
import es.upm.mabills.persistence.UserPersistence;
import es.upm.mabills.services.JwtService;
import es.upm.mabills.services.TokenCacheService;
import io.vavr.control.Try;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static es.upm.mabills.services.JwtService.ROLE_USER;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";
    private final JwtService jwtService;
    private final TokenCacheService tokenCacheService;
    private final UserPersistence userPersistence;
    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, TokenCacheService tokenCacheService, UserPersistence userPersistence) {
        this.jwtService = jwtService;
        this.tokenCacheService = tokenCacheService;
        this.userPersistence = userPersistence;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws IOException, ServletException {
        String token = jwtService.extractToken(request.getHeader(AUTHORIZATION));
        if (!token.isEmpty() && isValidToken(token)) {
            GrantedAuthority authority = new SimpleGrantedAuthority(ROLE_USER);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            buildUserPrincipal(jwtService.username(token)), token, List.of(authority));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private boolean isValidToken(String token) {
        return !tokenCacheService.isTokenBlackListed(token) && jwtService.isValidToken(token);
    }

    private UserPrincipal buildUserPrincipal(String username) {
        return Try.of(() -> userPersistence.findUserByUsername(username))
                .map(userEntity -> UserPrincipal.builder()
                        .id(userEntity.getId())
                        .username(userEntity.getUsername())
                        .build())
                .getOrElseThrow(() -> new UserNotFoundException(username));
    }
}
