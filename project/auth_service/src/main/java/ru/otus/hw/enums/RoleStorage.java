package ru.otus.hw.enums;

import org.springframework.security.core.GrantedAuthority;

public enum RoleStorage implements GrantedAuthority {
    USER,//покупатель
    SELLER,//продавец
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }

    public String getRole() {
        return "ROLE_" + getAuthority();
    }
}
