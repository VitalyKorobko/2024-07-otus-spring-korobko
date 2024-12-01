package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.hw.models.Role;
import java.util.Set;


@Data
@AllArgsConstructor
public class UserDto {
    private long id;

    private String username;

    private boolean enabled;

    private Set<Role> roles;

}
