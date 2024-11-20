package ru.otus.hw.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.mapper.UserMapper;
import ru.otus.hw.repositories.UserRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public UserDetailsServiceImpl(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        return mapper.toUserDto(repository.findByUsername(username));
    }


}
