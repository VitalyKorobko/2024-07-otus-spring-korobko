package ru.otus.hw.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AuthServiceImpl implements AuthService {
    private static final String AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer ";

    private final RestClient authRestClient;

    public AuthServiceImpl(@Qualifier("authRestClient") RestClient authRestClient) {
        this.authRestClient = authRestClient;
    }

    @Override
    public void reg(String token) {
        authRestClient.post()
                .uri("/api/v1/auth")
                .header(AUTHORIZATION, BEARER + token)
                .body(token)
                .retrieve();
    }
}
