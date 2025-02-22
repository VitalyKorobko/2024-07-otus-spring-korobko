package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.models.Order;
import ru.otus.hw.models.Role;
import ru.otus.hw.models.User;
import ru.otus.hw.enums.Status;
import ru.otus.hw.services.ProductService;
import ru.otus.hw.services.OrderService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.services.UserService;
import ru.otus.hw.services.check.CheckService;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    private final ProductService productService;

    private final OrderService orderService;

    private final PasswordEncoder passwordEncoder;

    private final CheckService checkService;

    //отслеживание перехода на страницу администратора
    @GetMapping("/admin")
    public String admin(Model model) {
        var admins = userService.findAllByRoles(Role.ADMIN);
        var sellers = userService.findAllByRoles(Role.SELLER);
        var users = userService.findAllByRoles(Role.USER);
        model.addAttribute("admins", admins);
        model.addAttribute("sellers", sellers);
        model.addAttribute("users", users);
        model.addAttribute("ADMIN", Role.ADMIN);
        model.addAttribute("SELLER", Role.SELLER);
        model.addAttribute("USER", Role.USER);
        return "admin";
    }

    //Отслеживание перехода на страницу продавца
    @GetMapping("/admin/seller-{id}")
    public String userItems(@PathVariable(value = "id") long userId, Model model) {
        var user = userService.findById(userId);
        model.addAttribute("products", productService.findAllByUser(user));
        model.addAttribute("user", user);
        return "seller-products";
    }

    //Отслеживание перехода на страницу пользователя
    @GetMapping("/admin/user-{id}")
    public String getUserOrders(@PathVariable(value = "id") String user_id, Model model) {
        var user = userService.findById(Long.parseLong(user_id));
        var orders = orderService.findByUser(user);
        model.addAttribute("orders", orders);
        model.addAttribute("user", user);
        model.addAttribute("CURRENT", Status.CURRENT);
        model.addAttribute("ISSUED", Status.ISSUED);
        model.addAttribute("PAID", Status.PAID);
        model.addAttribute("COMPLETED", Status.COMPLETED);
        return "user-orders";
    }

    //по этому адресу получаем форму изменения учетных данных пользователя
    @GetMapping("/admin/update/user-{id}")
    public String getUserUpdatePage(@RequestParam(name = "error", defaultValue = "", required = false) String error,
                             @RequestParam(name = "username", required = false) String username,
                             @RequestParam(name = "email", required = false) String email,
                             @PathVariable(value = "id") String user_id,
                             Model model) {
        var user = userService.findById(Long.parseLong(user_id));
        if (user == null) {
            model.addAttribute("error", "Пользователь c id = " + user_id + " не найден");
        }
        if (error.equals("username")) {
            model.addAttribute("error", "Пользователь <<" + username + ">> уже существует");
        }
        if (error.equals("incorrectEmail")) {
            model.addAttribute("error", "Некорректный email");
        }
        if (error.equals("emailDuplicate")) {
            model.addAttribute("error", "Пользователь с email: " + email + " уже существует!");
        }
        if (error.equals("differentPassword")) {
            model.addAttribute("error", "Пароли не совпадают");
        }
        if (error.equals("password")) {
            model.addAttribute("error", "Некорректный пароль, минимум 5 символов");
        }
        model.addAttribute("user", user);
        return "user-update";
    }

    //по этому адресу обрабатываем изменение учетных данных пользователя
    @PostMapping("/admin/update/user-{id}")
    String userUpdate(@PathVariable(value = "id") Long user_id,
                      @RequestParam("username") String username,
                      @RequestParam("email") String email,
                      @RequestParam("password") String password,
                      @RequestParam("repeatPassword") String repeatPassword,
                      @RequestParam("enabled") String enabled,
                      @RequestParam("roles") Set<Role> roles) {
        if (!checkService.checkUsernameByDuplicate(username, user_id)) {
            return "redirect:/admin/update/user-" + user_id + "?error=username&username=" + username;
        }
        if (!checkService.checkEmailByDuplicate(email, user_id)) {
            return "redirect:/admin/update/user-" + user_id + "?error=emailDuplicate&email=" + email;
        }
        if (!checkService.checkPasswords(password, repeatPassword)) {
            return "redirect:/admin/update/user-" + user_id + "?error=differentPassword";
        }
        var user = userService.findById(user_id);
        user.setUsername(username);
        user.setEmail(email);
        if (!password.equals(user.getPassword())) {//проверяем если пароль был изменён,то устанавливаем новый
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setRoles(roles);
        user.setEnabled(Boolean.parseBoolean(enabled));
        userService.save(user);
        return "redirect:/admin";
    }
}
