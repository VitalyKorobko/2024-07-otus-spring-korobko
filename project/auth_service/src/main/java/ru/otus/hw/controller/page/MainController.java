package ru.otus.hw.controller.page;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import ru.otus.hw.config.TokenStorage;
import ru.otus.hw.service.TokenService;

import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {
    private final TokenService tokenService;

    private final TokenStorage tokenStorage;

    private final RestClient client;

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
        sendToken(tokenStorage.getToken());

        System.out.println("\n=====================SUCCESS========================\n");
        System.out.println(tokenStorage.getToken());
    }

    private void sendToken(String token) {
        try {
            client
                    .post()
                    .uri("http://localhost:7778/api/v1/token")
                    .header("Authorization", "Bearer " + tokenStorage.getToken())
                    .body(tokenStorage.getToken())
                    .retrieve();
        } catch (Exception ex) {
            //todo retry
            log.warn("failed to send token");
        }
    }


}
