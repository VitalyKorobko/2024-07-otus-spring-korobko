package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import ru.otus.hw.dto.ProductDto;
import ru.otus.hw.mapper.ProductMapper;
import ru.otus.hw.models.Product;
import ru.otus.hw.models.Role;
import ru.otus.hw.models.User;
import ru.otus.hw.services.ProductService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

import static java.util.Objects.isNull;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    private final ProductMapper productMapper;

    //страница добавления нового товара, доступна для ADMIN и SELLER
    @GetMapping("/product/add")
    public String add(@RequestParam(value = "error", defaultValue = "", required = false) String error,
                      @RequestParam(value = "title", defaultValue = "", required = false) String title,
                      @RequestParam(value = "ref", defaultValue = "", required = false) String ref,
                      @RequestParam(value = "price", defaultValue = "0", required = false) String price,
                      @RequestParam(value = "description", defaultValue = "", required = false) String description,
                      Model model) {
        if (error.equals("imageUrl")) {
            model.addAttribute("message", "Запрос uri изображения не вернул 200 ОК");
            model.addAttribute("error", true);
            model.addAttribute("product", new Product(title, ref,
                    "", description, Integer.parseInt(price), null));
            return "add-product";
        }
        model.addAttribute("product", new Product());
        return "add-product";
    }

    //по этому адресу обрабатываем добавление нового товара
    @PostMapping("/product/add")
    public String addProduct(@AuthenticationPrincipal User user, @Valid ProductDto productDto,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", productMapper.toProduct(productDto, user));
            model.addAllAttributes(Map.of(
                            "message", bindingResult.getFieldError().getDefaultMessage(),
                            "error", true
                    )
            );
            return "add-product";
        }

        if (!productService.checkUrl(productDto.getImage())) {
            return "redirect:/product/add?error=imageUrl&title=%s&ref=%s&price=%d&description=%s"
                    .formatted(productDto.getTitle(), productDto.getRef(),
                            productDto.getPrice(), productDto.getDescription());
        }
        productService.create(productMapper.toProduct(productDto, user));
        return "redirect:/";
    }

    //страница просмотра информармации по товару
    @GetMapping("/product/{num}")
    public String showProduct(@PathVariable(value = "num") String productId,
                              @AuthenticationPrincipal User user,
                              Model model) {
        Product product = productService.findById(productId);
        if (isNull(product)) {
            return "product-has-been-removed";
        }
        //если товар уже есть в корзине передаем true, иначе false
        model.addAttribute("contains", productService.checkProductByCart(productId, user));
        model.addAttribute("user", user);
        model.addAttribute("ADMIN", Role.ADMIN);
        model.addAttribute("product", product);
        return "show-product";
    }

    //по этому адресу обрабатываме запрос на удаление товара
    @PostMapping("/product/{num}/delete")
    public String showProduct(@PathVariable(value = "num") String id) {
        Product product = productService.findById(id);
        productService.delete(product);
        return "redirect:/";
    }

    //страница для ввода данных для изменения данных товара
    @GetMapping("/product/{num}/update")
    public String getUpdateProduct(@PathVariable(value = "num") String id,
                                   @AuthenticationPrincipal User user,
                                   @RequestParam(value = "error", defaultValue = "", required = false) String error,
                                   Model model) {
        if (error.equals("imageUrl")) {
            model.addAttribute("message", "Запрос изображения не вернул 200 ОК");
            model.addAttribute("error", true);
        }
        Product product = productService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("ADMIN", Role.ADMIN);
        model.addAttribute("product", product);
        return "update-product";
    }

    //по этому адресу обрабатываем изменения товара
    @PostMapping("/product/{num}/update")
    public String updateProduct(@Valid ProductDto productDto, BindingResult bindingResult,
                                Model model, @AuthenticationPrincipal User user) {
        if (bindingResult.hasErrors()) {
            var seller = productService.findById(productDto.getId()).getUser();
            var product = productMapper.toProduct(productDto, seller);
            model.addAttribute("product", product);
            model.addAllAttributes(Map.of(
                            "message", bindingResult.getFieldError().getDefaultMessage(),
                            "error", true,
                            "user", user
                    )
            );
            return "update-product";
        }
        if (!productService.checkUrl(productDto.getImage())) {
            return "redirect:/product/%s/update?error=imageUrl".formatted(productDto.getId());
        }
        var seller = productService.findById(productDto.getId()).getUser();
        productService.update(productMapper.toProduct(productDto, seller));
        return "redirect:/product/" + productDto.getId();

    }


}

