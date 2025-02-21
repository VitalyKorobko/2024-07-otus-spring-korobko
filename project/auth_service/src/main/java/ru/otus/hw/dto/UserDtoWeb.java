package ru.otus.hw.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import ru.otus.hw.model.Role;

import java.util.Collection;
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
