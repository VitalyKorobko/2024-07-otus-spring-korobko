package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.models.Product;
import ru.otus.hw.enums.Status;
import ru.otus.hw.models.User;
import ru.otus.hw.services.OrderServiceImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderServiceImpl orderService;

    //страница корзины, выводим перечень всех позиций текущего заказа
    @GetMapping("/cart")
    String cart(Model model,
                @AuthenticationPrincipal User user) {
        System.out.println(user);
        var currentOrder = orderService.findByUserAndStatus(user, Status.CURRENT);
        //получаем мапу товар - количество для корзины
        Map<Product, Integer> mapProducts = orderService.getMapProductsByCart(currentOrder);
        model.addAttribute("mapProducts", mapProducts);
        model.addAttribute("total", orderService.getTotalByCartOrOrder(mapProducts));//передаем общую стоимость корзины
        model.addAttribute("order", currentOrder);
        return "cart";
    }

    //выводим всю информацию по запрашиваемому заказу
    @GetMapping("/order/{order_id}")
    String getOrder(Model model, @PathVariable(value = "order_id") String orderId) {
        var order = orderService.findById(orderId);
        //получаем мапу товар - количество для сформированного заказа
        Map<Product, Integer> mapProducts = orderService.getProductsByOrder(order);
        model.addAttribute("mapProducts", mapProducts);
        model.addAttribute("order", order);
        model.addAttribute("total", orderService.getTotalByCartOrOrder(mapProducts)); //передаем общую стоимость заказа
        model.addAttribute("CURRENT", Status.CURRENT);
        model.addAttribute("ISSUED", Status.ISSUED);
        model.addAttribute("PAID", Status.PAID);
        model.addAttribute("COMPLETED", Status.COMPLETED);
        return "order";
    }

    //по этому адресу просматриваем статус заказа
    @GetMapping("/order/{order_id}/status")
    String getOrderStatus(Model model,
                          @PathVariable(value = "order_id") String orderId) {
        var order = orderService.findById(orderId);
        model.addAttribute(order);
        return "order-status-update";
    }

    //по этому адресу обрабатываем изменение статуса заказа
    @PostMapping("/order/{order_id}/status")
    String setStatusOrder(Model model,
                          @PathVariable(value = "order_id") String orderId,
                          @RequestParam(value = "status") Status status) {
        var order = orderService.findById(orderId);
        order.setStatus(status);
        orderService.save(order);
        return "redirect:/order/" + orderId;
    }

    //по этому адресу сохраняем данные из корзины в БД
    @PostMapping("/cart/{order_id}")
    String changeCart(@PathVariable(value = "order_id") String orderId,
                      @RequestParam(name = "product_id") List<String> productId,
                      @RequestParam List<Integer> count) {
        var order = orderService.findById(orderId);
        order = orderService.saveOrderAsJson(order, productId, count);//сохраняем данные из корзины в объект order
        orderService.save(order);//сохраняем данные в бд
        return "redirect:/cart";
    }

    //на этой странице обрабатываем событие "оформить заказ"
    @PostMapping("/cart/place_an_order/{order_id}")
    String placeAnOrder(@PathVariable(value = "order_id") String orderId) {
        var order = orderService.findById(orderId);
        order.setStatus(Status.ISSUED);
        orderService.save(order);
        return "redirect:/user";
    }

    //на этой странице обрабатываем удаление какой-либо позиции из корзины
    @PostMapping("/cart/delete/{order_id}")
    String deleteProductFromCart(@PathVariable(value = "order_id") String orderId,
                                 @RequestParam(name = "product_id") String productId) {
        var order = orderService.findById(orderId);
        order = orderService.deleteProductFromCart(order, productId);
        orderService.save(order);
        return "redirect:/cart";
    }

    //на этой странице обрабатываем добавление позиции к корзину
    @PostMapping("/added/{num}")
    String addProduct(@PathVariable(value = "num") String productId,
                      @AuthenticationPrincipal User user) {
        var currentOrder = orderService.findByUserAndStatus(user, Status.CURRENT);
        currentOrder = orderService.addProductInCart(currentOrder, productId, user);//добавляем позицию в корзину
        orderService.save(currentOrder);
        return "redirect:/product/" + productId;
    }
}
