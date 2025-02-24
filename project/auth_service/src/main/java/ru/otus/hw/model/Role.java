package ru.otus.hw.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,//покупатель
    SELLER,//продавец
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
