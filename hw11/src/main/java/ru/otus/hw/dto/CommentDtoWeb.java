package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoWeb {
    private String id;

    @Size(min = 5, message = "Длина комментария должна быть не менее 5 символов")
    private String text;

    @NotBlank(message = "Выберите книгу")
    private String bookId;

    private String message;

}
