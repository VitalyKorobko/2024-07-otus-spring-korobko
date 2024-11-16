package ru.otus.hw.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoWeb {
    private long id;

    @Size(min = 5, message = "Длина комментария должна быть не менее 5 символов")
    private String text;

    @Positive(message = "Выберите книгу")
    private long bookId;

    private String message;

}
