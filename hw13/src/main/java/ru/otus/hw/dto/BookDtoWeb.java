package ru.otus.hw.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDtoWeb {
    private long id;

    @NotBlank(message = "Поле название книги не должно быть пустым")
    private String title;

    @Positive(message = "Выберите автора книги")
    private long authorId;

    @Size(min = 1, message = "Выберите жанры для книги")
    @NotNull(message = "Выберите жанры для книги")
    private Set<Long> setGenresId;

}
