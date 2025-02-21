package ru.otus.hw.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.config.TokenStorage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class TokenController {
//    private final TokenService tokenService;

    private final TokenStorage tokenStorage;

//    @PostMapping("/api/v1/token")
//    public String token(Authentication authentication) {
//        tokenStorage.setToken(tokenService.getToken(authentication));
//        return tokenStorage.getToken();
//    }

    @GetMapping("/api/v1/token")
    public String token() {
        return tokenStorage.getToken();
    }


}
