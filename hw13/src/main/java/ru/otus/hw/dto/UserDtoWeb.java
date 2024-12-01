package ru.otus.hw.dto;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoWeb {
    private long id;

    @Size(min = 3, message = "Логин должен быть не менее 3 символов")
    private String username;

    @Size(min = 4, message = "Пароль должен быть не менее 4 символов")
    private String password;

    @Size(min = 4, message = "Пароль должен быть не менее 4 символов")
    private String repeatPassword;

    @Size(min = 1, message = "Выберите роль")
    @NotNull(message = "Выберите роль")
    private Set<String> roles;
}
