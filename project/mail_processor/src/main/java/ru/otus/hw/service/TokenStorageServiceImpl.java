package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TokenStorageServiceImpl implements TokenStorageService {
    private final Map<String, String> tokens = new HashMap<>();

    @Override
    public Map<String, String> findAll() {
        return tokens;
    }

    @Override
    public void setAll(Map<String, String> tokens) {
        tokens.clear();
        tokens.putAll(tokens);
    }

    @Override
    public void add(String username, String token) {
        tokens.put(username, token);
    }

    @Override
    public String findByUsername(String username) {
        var token = tokens.get(username);
        return token;
    }






}
