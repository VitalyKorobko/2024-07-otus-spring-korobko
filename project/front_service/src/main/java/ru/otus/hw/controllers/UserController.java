package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import ru.otus.hw.config.TokenStorage;
import ru.otus.hw.dto.UserDto;
import ru.otus.hw.models.Order;
import ru.otus.hw.models.Role;
import ru.otus.hw.models.User;
import ru.otus.hw.enums.Status;
import ru.otus.hw.services.ProductService;
import ru.otus.hw.services.OrderService;
import ru.otus.hw.services.TokenService;
import ru.otus.hw.services.check.CheckService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.services.UserService;


import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final OrderService orderService;

    private final ProductService productService;

    private final PasswordEncoder passwordEncoder;

    private final CheckService checkService;

    private final TokenStorage tokenStorage;

    private final TokenService tokenService;

    //отслеживание перехода на страницу личного кабинета, выводим информацию в зависмости от роли пользователя
    @GetMapping("/user")
    public String user(@AuthenticationPrincipal User user,  Model model) {
        Iterable<Order> orders = orderService.findByUser(user);
        model.addAttribute("roles", user.getRoles());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("products", productService.findAllByUser(user));
        model.addAttribute("orders", orders);
        model.addAttribute("ADMIN", Role.ADMIN);
        model.addAttribute("SELLER", Role.SELLER);
        model.addAttribute("USER", Role.USER);
        model.addAttribute("CURRENT", Status.CURRENT);
        model.addAttribute("ISSUED", Status.ISSUED);
        model.addAttribute("PAID", Status.PAID);
        model.addAttribute("COMPLETED", Status.COMPLETED);
        return "user";
    }

    @GetMapping("/token")
    public String setToken(Authentication authentication) {
        tokenStorage.setToken(tokenService.getToken(authentication));
        log.info("was created token: \n%s".formatted(tokenStorage.getToken()));
        System.out.println("\n===========================success=====================================\n");
        return "redirect:/user";

    }

    //отслеживание перехода на страницу авторизации "/login"
    @GetMapping("/login")
    public String login(@RequestParam(name = "error", defaultValue = "", required = false) String error, Model model) {
        if (error.equals("username")) {
            model.addAttribute("error", "Логин или пароль введены неверно");
        }
        return "login";
    }

    @GetMapping("/reg")
    public String reg(@RequestParam(name = "error", defaultValue = "", required = false) String error,
                      @RequestParam(name = "username", required = false) String username,
                      @RequestParam(name = "email", required = false) String email,
                      Model model) {
        if (error.equals("usernameDuplicate")) {
            model.addAttribute("error", true);
            model.addAttribute("message", "Пользователь с username: %s уже существует".formatted(username));
        }
        if (error.equals("emailDuplicate")) {
            model.addAttribute("error", true);
            model.addAttribute("message", "Пользователь с email: %s уже существует!".formatted(email));
        }
        if (error.equals("differentPassword")) {
            model.addAttribute("error", true);
            model.addAttribute("message", "Пароли не совпадают");
        }
        model.addAttribute("userDto", new UserDto(username, null, null, email, null));
        return "reg";
    }

    @PostMapping("/reg")
    public String saveProduct(@Valid UserDto userDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userDto", userDto);
            model.addAllAttributes(Map.of(
                            "message", bindingResult.getFieldError().getDefaultMessage(),
                            "error", true));
            return "reg";
        }
        if (!checkService.checkUsernameByDuplicate(userDto.getUsername())) {
            return "redirect:/reg?error=usernameDuplicate&username=%s&email=%s"
                    .formatted(userDto.getUsername(), userDto.getEmail());
        }
        if (!checkService.checkEmailByDuplicateEmail(userDto.getEmail())) {
            return "redirect:/reg?error=emailDuplicate&email=%s&username=%s"
                    .formatted(userDto.getEmail(), userDto.getUsername());
        }
        if (!checkService.checkPasswords(userDto.getPassword(), userDto.getRepeatedPassword())) {
            return "redirect:/reg?error=differentPassword&username=%s&email=%s"
                    .formatted(userDto.getUsername(), userDto.getEmail());
        }
        userService.insert(userDto.getUsername(), passwordEncoder.encode(userDto.getPassword()),
                userDto.getEmail(), true,
                Set.of(Role.valueOf(userDto.getRole().getRoleName())));
        return "redirect:/login";
    }

}
