package ru.otus.hw.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.services.BookService;

import java.util.Objects;

@Controller
public class MainController {

    private static final String SUCCESS_MESSAGE_TEMPLATE = "Пользователь с именем %s успешно зарегистрирован";

    private static final String WRONG_AUTHENTICATED_MESSAGE = "Логин или пароль введены неверно";

    private final BookService bookService;

    public MainController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/login")
    @PreAuthorize("!isAuthenticated()")
    public String login(Model model,
                        @RequestParam(required = false, defaultValue = "") String error,
                        @RequestParam(required = false) String success,
                        @RequestParam(required = false) String username) {
        if (Objects.equals(error, "username")) {
            model.addAttribute("error", WRONG_AUTHENTICATED_MESSAGE);
        }
        if (Objects.equals(success, "ok")) {
            model.addAttribute("success",
                    SUCCESS_MESSAGE_TEMPLATE.formatted(username));
        }
        return "login";
    }

    @GetMapping("/")
    public String getBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "index";
    }
}
