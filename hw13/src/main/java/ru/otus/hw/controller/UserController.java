package ru.otus.hw.controller;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.otus.hw.dto.RoleDto;
import ru.otus.hw.dto.UserDtoWeb;
import ru.otus.hw.repositories.UserRepository;
import ru.otus.hw.services.RoleService;
import ru.otus.hw.services.UserService;

import java.util.List;
import java.util.Objects;


@Controller
@RequestMapping("/user")
public class UserController {
    private static final String ERROR_MESSAGE_ABOUT_DIFFERENT_PASSWORD = "Пароли не совпадают";

    private static final String ERROR_MESSAGE_ABOUT_EMPTY_AGE_FIELD = "Введите возраст";

    private static final String ERROR_MESSAGE_LOGIN_ALREADY_EXIST = "Пользователь с таким логином уже существует";

    private static final String REDIRECT_TEMPLATE = "redirect:/login?success=ok&username=%s";

    private final RoleService roleService;

    private final UserService userService;

    private final UserRepository userRepository;

    public UserController(RoleService roleService, UserService userService, UserRepository userRepository) {
        this.roleService = roleService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/reg")
    @PreAuthorize("!isAuthenticated()")
    public String getNewUserPage(Model model) {
        model.addAttribute("roles", roleService.findAll());
        return "add-user";
    }

    @GetMapping("/")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }

    @PostMapping("/reg")
    @PreAuthorize("!isAuthenticated()")
    public String addNewUser(@Valid @ModelAttribute UserDtoWeb userDtoWeb,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            var message = bindingResult.getFieldError().getDefaultMessage();
            message = checkAge(message);
            addAttributes(message, roleService.findAll(), userDtoWeb, model);
            return "add-user";
        } else if (!checkPassword(userDtoWeb.getPassword(), userDtoWeb.getRepeatPassword())) {
            addAttributes(ERROR_MESSAGE_ABOUT_DIFFERENT_PASSWORD, roleService.findAll(), userDtoWeb, model);
            return "add-user";
        } else if (checkUsername(userDtoWeb.getUsername())) {
            addAttributes(ERROR_MESSAGE_LOGIN_ALREADY_EXIST, roleService.findAll(), userDtoWeb, model);
            return "add-user";
        }
        userService.insert(userDtoWeb.getUsername(),
                userDtoWeb.getPassword(), userDtoWeb.getRoles());
        return REDIRECT_TEMPLATE.formatted(userDtoWeb.getUsername());
    }

    private boolean checkUsername(String username) {
        return userRepository.findAll().stream()
                .map(u -> u.getUsername())
                .toList()
                .contains(username);
    }

    private void addAttributes(String message, List<RoleDto> roles, UserDtoWeb userDtoWeb, Model model) {
        model.addAttribute("error", message);
        model.addAttribute("roles", roles);
        model.addAttribute("username", userDtoWeb.getUsername());
    }

    private boolean checkPassword(String password, String repeatPassword) {
        return Objects.equals(password, repeatPassword);
    }

    private String checkAge(String message) {
        if (message.contains("Failed to convert property value of type")) {
            return ERROR_MESSAGE_ABOUT_EMPTY_AGE_FIELD;
        }
        return message;
    }

}
