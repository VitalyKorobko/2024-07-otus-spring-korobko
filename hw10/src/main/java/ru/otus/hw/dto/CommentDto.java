package ru.otus.hw.dto;

//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private long id;

//    @NotBlank(message = "Поле не должно быть пустым")
//    @Size(min = 5, message = "Поле должно быть минимум 5 символов")
    private String text;

    private long bookId;

}
