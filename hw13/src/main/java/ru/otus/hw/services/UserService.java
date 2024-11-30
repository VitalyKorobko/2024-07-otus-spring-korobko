package ru.otus.hw.services;

import ru.otus.hw.models.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    User insert(String username, String password, short age, Set<String> roles);

    List<User> findAll();
}
