package ru.otus.hw.services;

import ru.otus.hw.enums.Status;
import ru.otus.hw.models.Product;
import ru.otus.hw.models.Order;
import ru.otus.hw.models.User;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Order findByUserAndStatus(User user, Status status);

    List<Order> findByUser(User user);

    Map<Product, Integer> getMapProductsByCart(Order currentOrder);//получаем мапу товар - количество для корзины

    Order findById(String id);

    Order save(Order order);

    Map<Product, Integer> getProductsByOrder(Order order);

    Order saveOrderAsJson(Order order, List<String> productId, List<Integer> count);

    Order addProductInCart(Order currentOrder, String productId, User user);

    Order deleteProductFromCart(Order order, String productId);

    int getTotalByCartOrOrder(Map<Product, Integer> mapProducts);


}


