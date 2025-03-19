package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RegServiceImpl implements RegService {
    private final UserRepository userRepository;

    @Override
    public boolean checkEmailByDuplicateEmail(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    @Override
    public boolean checkPasswords(String password, String repeatPassword) {
        return password.equals(repeatPassword);
    }

}