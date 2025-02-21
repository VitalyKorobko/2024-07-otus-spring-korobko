package ru.otus.hw.service;

import ru.otus.hw.model.Role;
import ru.otus.hw.model.User;

import java.util.List;
import java.util.Set;


public interface UserService {
    User findByUserName(String username);

    User findByEmail(String email);

    User findById(long id);

    List<User> findAll();

    User insert(String username, String password, String email, boolean enabled, Set<Role> roles);

    User update(long id, String username, String password, String email, boolean enabled, Set<Role> roles);

    void deleteById(long id);

    List<User> findAllByRoles(Set<Role> roles);
}
