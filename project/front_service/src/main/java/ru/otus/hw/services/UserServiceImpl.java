package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.exception.EntityNotFoundException;
import ru.otus.hw.models.Role;
import ru.otus.hw.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.hw.repositories.UserRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;

    private final ProductService productService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("user with username: %s not found".formatted(username)));
    }

    @Override
    public List<User> findAllByRoles(Role role) {
        return userRepository.findAllByRoles(role);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("user with email %s not found".formatted(email)));
    }

    @Override
    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("user with id %d not found".formatted(id)));
    }

    @Override
    public User insert(String username, String password, String email, boolean enabled, Set<Role> roles) {
        return save(new User(username, password, email, enabled, roles));
    }

    @Override
    public User update(long id, String username, String password, String email, boolean enabled, Set<Role> roles) {
        return new User(id, username, password, email, enabled, roles);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public int getCountSellers() {
        return findAllByRoles(Role.SELLER).size();
    }

    @Override
    public int getCountProducts() {
        var sellers = findAllByRoles(Role.SELLER);
        return sellers.stream()
                .mapToInt(user -> productService.findAllByUser(user).size())
                .sum();
    }


}

