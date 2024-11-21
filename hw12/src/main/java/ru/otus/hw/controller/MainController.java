package ru.otus.hw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.services.BookService;

import java.util.Objects;

@Controller
public class MainController {

    private final BookService bookService;

    public MainController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/login")
    public String login(Model model, @RequestParam(required = false, defaultValue = "") String error) {
        if (Objects.equals(error, "username")) {
            model.addAttribute("error", "Логин или пароль введены неверно");
        }
        return "login";
    }

    @GetMapping("/")
    public String getBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "index";
    }
}
