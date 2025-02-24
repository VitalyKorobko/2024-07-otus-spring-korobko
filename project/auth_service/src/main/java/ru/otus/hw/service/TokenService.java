package ru.otus.hw.service;

import org.springframework.security.core.Authentication;

public interface TokenService {

    String getToken(Authentication authentication);

    void sendToken(String token, String url);

}
