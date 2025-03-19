package ru.otus.hw.models;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    USER,//покупатель

    SELLER,//продавец

    ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_%s".formatted(name());
    }
}
