package ru.otus.hw.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.config.TokenStorage;
import ru.otus.hw.service.TokenService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;

    private final TokenStorage tokenStorage;

    @PostMapping("/api/v1/auth")
    public void regService(Authentication authentication, @RequestBody String token) {
        send(token);
    }

    private void send(String token) {
        log.info("token was sent: %s".formatted(token));
        tokenService.sendToken(token, "http://localhost:7773/api/v1/token");
        tokenService.sendToken(token, "http://localhost:7774/api/v1/token");
        tokenService.sendToken(token, "http://localhost:7775/api/v1/token");
        tokenService.sendToken(token, "http://localhost:7776/api/v1/token");
        tokenService.sendToken(token, "http://localhost:7777/api/v1/token");
        tokenService.sendToken(token, "http://localhost:7778/api/v1/token");
    }
}
