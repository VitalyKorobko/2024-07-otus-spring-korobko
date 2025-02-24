package ru.otus.hw.controller.page;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.config.TokenStorage;
import ru.otus.hw.service.TokenService;

import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final TokenStorage tokenStorage;

    private final TokenService tokenService;

    @GetMapping("/login")
    public String login(Model model, @RequestParam(required = false, defaultValue = "") String error) {
        if (Objects.equals(error, "username")) {
            model.addAttribute("error", "Логин или пароль введены неверно");
        }
        return "login";
    }

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        reg(authentication);
        return "index";
    }

    private void reg(Authentication authentication) {
        tokenStorage.setToken(tokenService.getToken(authentication));
        sendToken();
    }

    private void sendToken() {
        log.info(tokenStorage.getToken());
        tokenService.sendToken(tokenStorage.getToken(), "http://localhost:7773/api/v1/token");
        tokenService.sendToken(tokenStorage.getToken(), "http://localhost:7774/api/v1/token");
        tokenService.sendToken(tokenStorage.getToken(), "http://localhost:7775/api/v1/token");
        tokenService.sendToken(tokenStorage.getToken(), "http://localhost:7776/api/v1/token");
        tokenService.sendToken(tokenStorage.getToken(), "http://localhost:7777/api/v1/token");
        tokenService.sendToken(tokenStorage.getToken(), "http://localhost:7778/api/v1/token");
    }


}
