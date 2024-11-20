package ru.otus.hw.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.models.Role;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.UserRepository;
import ru.otus.hw.services.BookService;

import java.util.Objects;
import java.util.Set;

@Controller
public class MainController {

    private final BookService bookService;

    public MainController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String getBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model, @RequestParam(required = false, defaultValue = "") String error) {
        if (Objects.equals(error, "username")) {
            model.addAttribute("error", "Логин или пароль введены неверно");
        }
        return "login";
    }

}
