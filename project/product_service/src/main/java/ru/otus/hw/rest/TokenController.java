package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.config.TokenStorage;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenController {
    private final TokenStorage tokenStorage;

    @PostMapping(value = "/api/v1/token")
    public void saveToken(@RequestBody String token) {
        tokenStorage.setToken(token);
        log.info("save token");
    }
}
