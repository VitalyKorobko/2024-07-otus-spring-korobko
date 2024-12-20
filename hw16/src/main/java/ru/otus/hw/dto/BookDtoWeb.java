package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDtoWeb {
    private long id;

    @NotBlank(message = "Поле название книги не может быть пустым")
    private String title;

    @Positive(message = "Выберите автора книги")
    private long authorId;

    @Size(min = 1, message = "Выберите жанры для книги")
    private Set<Long> setGenresId;

    private String message;
}
