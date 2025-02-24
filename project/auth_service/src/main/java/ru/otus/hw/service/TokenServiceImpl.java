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

import java.time.Instant;
import java.util.stream.Collectors;


@Service
@Slf4j
public class TokenServiceImpl implements TokenService {
    private static final String AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer ";

    private final JwtEncoder encoder;

    private final RestClient client;

    private final TokenStorage tokenStorage;

    private final int jwtExpire;

    public TokenServiceImpl(JwtEncoder encoder, RestClient client,
                            TokenStorage tokenStorage,
                            @Value("${jwt.expire}") int jwtExpire) {
        this.encoder = encoder;
        this.client = client;
        this.tokenStorage = tokenStorage;
        this.jwtExpire = jwtExpire;
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
                    .header(AUTHORIZATION, BEARER + tokenStorage.getToken())
                    .body(tokenStorage.getToken())
                    .retrieve();
        } catch (Exception ex) {
            log.warn("error sending token for {}", uri);
        }
        log.info("token was sent to the service {}", uri);
    }


}