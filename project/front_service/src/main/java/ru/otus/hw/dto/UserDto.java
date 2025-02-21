package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
//    @Size(
//            min = 5,
//            max = 15,
//            message = "Длина логина не должна быть мене 3 символов и не более 15")
    @Pattern(
            regexp = "(^[a-z0-9](-?[a-z0-9])*$)",
            message = "Неверный формат логина, логин должен содержать только буквы и цифры"
    )
    private String username;

    @Size(min = 6, message = "Длина пароля не должна быть менее 6 символов")
    private String password;

    @Size(min = 6, message = "Длина пароля не должна быть менее 6 символов")
    private String repeatedPassword;

    @Pattern(
            regexp = "([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+)",
            message = "Неверный формат email"
    )
    private String email;

    private RoleDto role;

}

