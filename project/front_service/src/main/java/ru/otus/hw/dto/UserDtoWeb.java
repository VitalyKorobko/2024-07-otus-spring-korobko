package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.hw.models.Role;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserDtoWeb {
    private long id;

    private String username;

    private String password;

    private String email;

    private boolean enabled;

    private Set<Role> roles;

    private String message;
}
