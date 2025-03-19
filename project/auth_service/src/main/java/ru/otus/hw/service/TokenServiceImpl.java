package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.otus.hw.exception.TokenException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class TokenServiceImpl implements TokenService {
    private static final String AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer ";

    private final List<String> tokens = new ArrayList<>();

    private final JwtEncoder encoder;

    private final RestClient client;

    private final int jwtExpire;

    public TokenServiceImpl(JwtEncoder encoder, RestClient client,
                            @Value("${jwt.expire}") int jwtExpire) {
        this.encoder = encoder;
        this.client = client;
        this.jwtExpire = jwtExpire;
    }

    @Override
    public String add(Authentication authentication) {
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
        var token = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        tokens.add(token);
        return token;
        // @formatter:on
    }

    @Override
    @Retryable(retryFor = {Exception.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    public void send(String token, String uri) {
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

    @Override
    @Retryable(retryFor = {Exception.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 3))
    public void sendAll(String uri) {
        try {
            client
                    .post()
                    .uri(uri)
                    .header(AUTHORIZATION, BEARER + tokens.get(0))
                    .body(tokens)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve();
        } catch (Exception e) {
            log.warn("error sending tokens for {}: {} : {}", uri, e, e.getMessage());
            throw new TokenException("tokens wasn't sent to %s".formatted(uri));
        }
        log.info("tokens was sent to the service {}", uri);
    }


}