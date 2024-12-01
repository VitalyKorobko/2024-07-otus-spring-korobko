package ru.otus.hw.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.UserDto;
import ru.otus.hw.models.User;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return new UserDto(
          user.getId(),
          user.getUsername(),
          user.isEnabled(),
          user.getAge(),
          user.getRoles()
        );
    }

}
