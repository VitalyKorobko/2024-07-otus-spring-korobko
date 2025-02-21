package ru.otus.hw.repositories;

import ru.otus.hw.models.Order;
import ru.otus.hw.models.User;

import java.util.List;
import java.util.Optional;

public interface OrderRepository{

    List<Order> findByUser(User user);

    Optional<Order> findById(String id);

    Order create(Order order);

    Order update(Order order);
}
