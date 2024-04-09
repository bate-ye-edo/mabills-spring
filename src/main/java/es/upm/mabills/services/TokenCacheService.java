package es.upm.mabills.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenCacheService {
    private static final Logger LOGGER = LogManager.getLogger(TokenCacheService.class);

    private final JwtService jwtService;
    private final Set<String> tokenBlackListCache = ConcurrentHashMap.newKeySet();

    @Autowired
    public TokenCacheService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public boolean isTokenBlackListed(String token) {
        return tokenBlackListCache.contains(token);
    }

    public void blackListToken(String token) {
        tokenBlackListCache.add(token);
    }

    @Scheduled(fixedRate = 60000)
    public void cleanTokenCache() {
        if(!tokenBlackListCache.isEmpty()) {
            tokenBlackListCache.removeIf(jwtService::isTokenExpired);
            LOGGER.info("Cleared expired black listed tokens");
        }
    }

}
