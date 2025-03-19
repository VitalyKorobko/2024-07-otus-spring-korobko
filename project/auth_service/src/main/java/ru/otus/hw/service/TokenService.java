package ru.otus.hw.service;

import org.springframework.security.core.Authentication;


public interface TokenService {

    String add(Authentication authentication);

    void sendAll(String url);

    void send(String token, String url);
}
