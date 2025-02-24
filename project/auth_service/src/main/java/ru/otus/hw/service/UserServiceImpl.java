package ru.otus.hw.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exception.EntityNotFoundException;
import ru.otus.hw.model.Role;
import ru.otus.hw.model.User;
import ru.otus.hw.repository.UserRepository;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        return findUser(username);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUserName(String username) {
        return findUser(username);
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с email: %s не найден".formatted(email)));
    }

    @Override
    public User findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id %d не найден".formatted(id)));
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User create(String username, String password, String email, boolean enabled, Set<Role> roles) {
        return save(0, username, password, email, enabled, roles);
    }

    @Override
    public User update(long id, String username, String password, String email, boolean enabled, Set<Role> roles) {
        return save(id, username, password, email, enabled, roles);
    }

    @Override
    public void deleteById(long id) {
        repository.deleteById(id);
    }

    @Override
    public List<User> findAllByRoles(Set<Role> roles) {
        return repository.findAllByRoles(roles);
    }

    private User findUser(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь %s не найден".formatted(username)));
    }

    private User save(long id, String username, String password, String email, boolean enabled, Set<Role> roles) {
        var user = new User(id, username, password, email, enabled, roles);
        return repository.save(user);
    }
}