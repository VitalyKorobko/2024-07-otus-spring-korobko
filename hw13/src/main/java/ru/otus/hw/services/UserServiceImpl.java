package ru.otus.hw.services;

import org.springframework.security.access.prepost.PostFilter;
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

    private final AclServiceWrapperService aclServiceWrapperService;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder encoder, AclServiceWrapperService aclServiceWrapperService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.aclServiceWrapperService = aclServiceWrapperService;
    }

    @Override
    @Transactional
    public User insert(String username, String password, Set<String> roles) {
        var user = userRepository.save(new User(0L, username,
                encoder.encode(password), true, roleRepository.findAllByNameIn(roles)));
        aclServiceWrapperService.createPermissionsByUser(user);
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, 'READ')")
    public List<User> findAll() {
        return userRepository.findAll();
    }


}
