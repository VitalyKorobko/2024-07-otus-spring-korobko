package ru.otus.hw.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.config.TokenStorage;
import ru.otus.hw.service.TokenService;

import java.util.concurrent.ThreadPoolExecutor;

@RestController
@Slf4j
public class AuthController {
    private final TokenService tokenService;

    private final TokenStorage tokenStorage;

    private final String host;

    private final String port;

    private final ThreadPoolExecutor executor;

    public AuthController(TokenStorage tokenStorage,
                          TokenService tokenService,
                          @Value("${app.gateway.host}") String host,
                          @Value("${app.gateway.port}") String port,
                          ThreadPoolExecutor executor) {
        this.tokenStorage = tokenStorage;
        this.tokenService = tokenService;
        this.host = host;
        this.port = port;
        this.executor = executor;
    }

    @PostMapping("/api/v1/auth")
    public void regService(Authentication authentication, @RequestBody String token) {
        tokenStorage.setToken(tokenService.getToken(authentication));
        send(token);
    }

    private void send(String token) {
        log.info("token was sent: %s".formatted(token));
        executor.execute(() ->
                tokenService.sendToken(token, "%s:%s/product/api/v1/token".formatted(host, port)));
        executor.execute(() ->
                tokenService.sendToken(token, "%s:%s/order/api/v1/token".formatted(host, port)));
        executor.execute(() ->
                tokenService.sendToken(token, "%s:%s/storage/api/v1/token".formatted(host, port)));
        executor.execute(() ->
                tokenService.sendToken(token, "%s:%s/mail_client/api/v1/token".formatted(host, port)));
        executor.execute(() ->
                tokenService.sendToken(token, "%s:%s/mail_processor/api/v1/token".formatted(host, port)));
        executor.execute(() ->
                tokenService.sendToken(token, "%s:%s/notification/api/v1/token".formatted(host, port)));
    }
}
