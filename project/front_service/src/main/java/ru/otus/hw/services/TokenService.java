package ru.otus.hw.services;

import org.springframework.security.core.Authentication;

public interface TokenService {

    String getToken(Authentication authentication);
}
