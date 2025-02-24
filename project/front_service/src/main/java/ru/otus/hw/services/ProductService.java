package ru.otus.hw.services;

import ru.otus.hw.models.Product;
import ru.otus.hw.models.User;

import java.util.List;


public interface ProductService {
    Product create(Product product);

    Product update(Product product);

    List<Product> findAllByUser(User user);

    List<Product> findAll();

    Product findById(String id);

    void delete(Product product);

    boolean checkFieldLength(String string);

    boolean checkUrl(String stringUrl);

    boolean checkProductByCart(String productId, User user);

    boolean checkPrice(String price);

}
