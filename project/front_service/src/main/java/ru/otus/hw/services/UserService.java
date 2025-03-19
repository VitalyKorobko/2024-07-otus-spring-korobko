package ru.otus.hw.services;

import ru.otus.hw.models.Role;
import ru.otus.hw.models.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    User findByUsername(String username);

    List<User> findAllByRoles(Role role);

    User findByEmail(String email);

    User findById(long id);

    User insert(String username, String password, String email, boolean enabled, Set<Role> roles);

    User update(long id, String username, String password, String email, boolean enabled, Set<Role> roles);

    User save(User user);

    int getCountSellers();

    int getCountProducts();

}
