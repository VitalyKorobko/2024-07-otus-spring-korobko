package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.otus.hw.config.TokenStorage;
import ru.otus.hw.exception.TokenException;

import java.time.Instant;
import java.util.stream.Collectors;


@Service
@Slf4j
public class TokenServiceImpl implements TokenService {
    private static final String AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer ";

    private final JwtEncoder encoder;

    private final RestClient client;

    private final int jwtExpire;

    private final TokenStorage tokenStorage;

    public TokenServiceImpl(JwtEncoder encoder, RestClient client,
                            @Value("${jwt.expire}") int jwtExpire, TokenStorage tokenStorage) {
        this.encoder = encoder;
        this.client = client;
        this.jwtExpire = jwtExpire;
        this.tokenStorage = tokenStorage;
    }

    @Override
    public String getToken(Authentication authentication) {
        Instant now = Instant.now();
        // @formatter:off
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(jwtExpire))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        // @formatter:on
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public void sendToken(String token, String uri) {
        try {
            client
                    .post()
                    .uri(uri)
                    .header(AUTHORIZATION, BEARER + token)
                    .body(token)
                    .retrieve();
        } catch (Exception ex) {
            log.warn("error sending token for {}", uri);
            throw new TokenException("token wasn't sent to %s".formatted(uri));
        }
        log.info("token was sent to the service {}", uri);
    }


}