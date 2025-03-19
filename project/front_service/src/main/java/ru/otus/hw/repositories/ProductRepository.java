package ru.otus.hw.repositories;

import ru.otus.hw.models.Product;
import ru.otus.hw.models.User;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product create(Product product);

    Product update(Product product);

    List<Product> findAllByUser(User user);

    List<Product> findAll();

    Optional<Product> findById(String id);

    void delete(Product product);
}
