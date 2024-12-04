package ru.otus.hw.enums;

public enum RoleType {
    ADMIN("ADMIN"),

    USER("USER");

    private static final String AUTHORITY_TEMPLATE = "ROLE_%s";

    private final String value;

    RoleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getAuthority() {
        return AUTHORITY_TEMPLATE.formatted(value);
    }
}
