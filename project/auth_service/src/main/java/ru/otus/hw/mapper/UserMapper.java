package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.UserDtoWeb;
import ru.otus.hw.model.User;

@Component
public class UserMapper {

    public UserDtoWeb toUserDtoWeb(User user, String message) {
        return new UserDtoWeb(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.isEnabled(),
                user.getRoles(),
                message
        );
    }
}
