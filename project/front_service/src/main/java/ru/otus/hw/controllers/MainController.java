package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClient;
import ru.otus.hw.services.ProductService;
import ru.otus.hw.services.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    private final UserServiceImpl userService;

    private final ProductService productService;

    private final RestClient prodClient;

    public MainController(UserServiceImpl userService,
                          @Qualifier("productRestClient") RestClient prodClient,
                          ObjectMapper mapper, ProductService productService) {
        this.userService = userService;
        this.prodClient = prodClient;
        this.productService = productService;
    }

    //главная страница, выводим все товары в бд на главной странице
    @GetMapping("/")
    String index(Model model) {
        var products = productService.findAll();
        System.out.println();
        model.addAttribute("products", products);
        return "index";
    }

    //на этой странице выводим количество продавцов и общее количество товаров в бд
    @GetMapping("/about-us")
    public String about(Model model) {
        model.addAttribute("countSellers", userService.getCountSellers());
        model.addAttribute("countProducts", userService.getCountProducts());
        return "about";
    }
}
