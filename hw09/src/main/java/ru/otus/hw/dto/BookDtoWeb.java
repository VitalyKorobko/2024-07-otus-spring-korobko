package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDtoWeb {

    private long id;

    @NotBlank(message = "Поле не должно быть пустым")
    private String title;

    private long authorId;

    private Set<Long> setGenresId;
}
