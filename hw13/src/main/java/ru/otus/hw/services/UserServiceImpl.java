package ru.otus.hw.services;

import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.RoleRepository;
import ru.otus.hw.repositories.UserRepository;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Override
    @Transactional
//    @PreAuthorize("if (getAuthentication()==null)) {return true} else {return false}" )
//    @PreAuthorize("isAuthenticated()")
//    @PostAuthorize("isAuthenticated()")
    public User insert(String username, String password, short age, Set<String> roles) {
        return userRepository.save(new User(0L, username,
                encoder.encode(password), true, age, roleRepository.findAllByNameIn(roles)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
