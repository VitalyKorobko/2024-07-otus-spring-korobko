package ru.otus.hw.controller.page;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.UserDto;
import ru.otus.hw.model.Role;
import ru.otus.hw.service.RegServiceImpl;
//import ru.otus.hw.service.RoleService;
import ru.otus.hw.service.UserService;

import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

//    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    private final RegServiceImpl regService;

    @GetMapping("/reg")
    public String reg(@RequestParam(name = "error", defaultValue = "", required = false) String error,
                      @RequestParam(name = "username", required = false) String username,
                      @RequestParam(name = "email", required = false) String email,
                      Model model) {
        if (error.equals("emailDuplicate")) {
            model.addAttribute("error", true);
            model.addAttribute("message", "Пользователь с email: " + email + " уже существует!");
        }
        if (error.equals("differentPassword")) {
            model.addAttribute("error", true);
            model.addAttribute("message", "Пароли не совпадают");
        }
        model.addAttribute("userDto", new UserDto(username, null, null, email, null));
//        model.addAttribute("email", email);
//        model.addAttribute("userDto", new UserDto());
        return "reg";
    }

    @PostMapping("/reg")
    public String saveProduct(
//            @Valid @ModelAttribute("product") UserDto userDto,
            @Valid UserDto userDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            addAllAttributes(model, userDto);
            model.addAllAttributes(Map.of(
                            "message", bindingResult.getFieldError().getDefaultMessage(),
                            "error", true
                    )
            );
            return "reg";
        }
        if (!regService.checkEmailByDuplicateEmail(userDto.getEmail())) {
            return "redirect:/reg?error=emailDuplicate&email=" + userDto.getEmail() + "&username=" + userDto.getUsername();
        }
        if (!regService.checkPasswords(userDto.getPassword(), userDto.getRepeatedPassword())) {
            return "redirect:/reg?error=differentPassword" + "&username=" + userDto.getUsername() + "&email=" + userDto.getEmail();
        }
        userService.insert(
                userDto.getUsername(),
                passwordEncoder.encode(userDto.getPassword()),
                userDto.getEmail(),
                true,
                Set.of(Role.valueOf(userDto.getRole().getRoleName()))
        );
        return "redirect:/login";
    }

    private void addAllAttributes(Model model, UserDto userdto) {
        addAllAttributes(model, Map.of("userDto", userdto));
    }

    private void addAllAttributes(Model model, Map<String, Object> map) {
        for (String key : map.keySet()) {
            model.addAttribute(key, map.get(key));
        }
    }

}
//
//@Controller
//@RequestMapping("/product")
//public class ProductController {
//    private final ProService productService;
//
//    private final AuthorService authorService;
//
//    private final GenreService genreService;
//
//    private final ProductMapper mapper;
//
//    public ProductController(ProductService productService, AuthorService authorService,
//                          GenreService genreService, ProductMapper mapper) {
//        this.productService = productService;
//        this.authorService = authorService;
//        this.genreService = genreService;
//        this.mapper = mapper;
//    }
//
//    @GetMapping("/{id}")
//    public String editProduct(@PathVariable("id") long id, Model model) {
//        var productDto = productService.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Товар с id = %d не найдена".formatted(id)));
//        addAllAttributes(model, mapper.toProductDtoWeb(productDto));
//        return "update-product";
//    }
//
//    @GetMapping("/new")
//    public String addProduct(Model model) {
//        addAllAttributes(model, new ProductDtoWeb());
//        return "add-product";
//    }
//
//    @PostMapping("/new")
//    public String saveProduct(
//            @Valid @ModelAttribute("product") ProductDtoWeb product,
//            BindingResult bindingResult,
//            Model model
//    ) {
//        if (bindingResult.hasErrors()) {
//            addAllAttributes(model, product);
//            model.addAllAttributes(Map.of(
//                            "message", bindingResult.getFieldError().getDefaultMessage(),
//                            "error", true
//                    )
//            );
//            return "add-product";
//        }
//        productService.insert(
//                product.getTitle(),
//                product.getAuthorId(),
//                product.getSetGenresId()
//        );
//        return "redirect:/";
//    }
//
//    @PostMapping("/{id}/update")
//    public String updateProduct(
//            @Valid @ModelAttribute("product") ProductDtoWeb product,
//            BindingResult bindingResult,
//            Model model
//    ) {
//        if (bindingResult.hasErrors()) {
//            addAllAttributes(model, product);
//            return "update-product";
//        }
//        productService.update(
//                product.getId(),
//                product.getTitle(),
//                product.getAuthorId(),
//                product.getSetGenresId()
//        );
//        return "redirect:/";
//    }
//
//    @PostMapping("/del/{id}")
//    public String deleteProduct(@PathVariable long id) {
//        productService.deleteById(id);
//        return "redirect:/";
//    }
//
//    private void addAllAttributes(Model model, ProductDtoWeb product) {
//        addAllAttributes(model, Map.of("product", product,
//                "authors", authorService.findAll(),
//                "genres", genreService.findAll())
//        );
//    }
//
//    private void addAllAttributes(Model model, Map<String, Object> map) {
//        for (String key : map.keySet()) {
//            model.addAttribute(key, map.get(key));
//        }
//    }
//
//
//}

