package ru.otus.hw.service;

import java.util.Map;

public interface TokenStorageService {
    Map<String, String> findAll();

    String findByUsername(String username);

    void setAll(Map<String, String> tokens);

    void add(String username, String token);

}
