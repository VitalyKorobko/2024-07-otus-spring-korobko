package ru.otus.hw.dto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private long id;

    @Size(min = 5, message = "Текст комментария должен быть минимум 5 символов")
    private String text;

    @Positive(message = "Выберите книгу")
    private long bookId;

}
