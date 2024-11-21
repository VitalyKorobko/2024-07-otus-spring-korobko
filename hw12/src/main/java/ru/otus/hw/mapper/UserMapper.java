package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.UserDto;
import ru.otus.hw.models.Role;
import ru.otus.hw.models.User;

import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.getRoles().stream().map(Role::new).collect(Collectors.toSet())
        );
    }
}
