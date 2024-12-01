package ru.otus.hw.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.otus.hw.models.Role;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;

    private String username;

    private boolean enabled;

    private short age;

    private Set<Role> roles;

}
