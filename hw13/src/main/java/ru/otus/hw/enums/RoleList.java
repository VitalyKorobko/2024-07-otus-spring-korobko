package ru.otus.hw.enums;

public enum RoleList {
    ADMIN("ADMIN"),

    PUBLISHER("PUBLISHER"),

    USER("USER");

    private final String value;

    RoleList(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
