package ru.otus.hw.controller.page;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class MainController {

    private final TokenStorage tokenStorage;

    private final TokenService tokenService;

    private final String host;

    private final String port;

    public MainController(TokenStorage tokenStorage,
                          TokenService tokenService,
                          @Value("${app.gateway.host}") String host,
                          @Value("${app.gateway.port}") String port) {
        this.tokenStorage = tokenStorage;
        this.tokenService = tokenService;
        this.host = host;
        this.port = port;
    }


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
        tokenService.sendToken(tokenStorage.getToken(), "%s:%s/product/api/v1/token".formatted(host, port));
        tokenService.sendToken(tokenStorage.getToken(), "%s:%s/order/api/v1/token".formatted(host, port));
        tokenService.sendToken(tokenStorage.getToken(), "%s:%s/storage/api/v1/token".formatted(host, port));
        tokenService.sendToken(tokenStorage.getToken(), "%s:%s/mail_client/api/v1/token".formatted(host, port));
        tokenService.sendToken(tokenStorage.getToken(), "%s:%s/mail_processor/api/v1/token".formatted(host, port));
        tokenService.sendToken(tokenStorage.getToken(), "%s:%s/notification/api/v1/token".formatted(host, port));
    }


}
