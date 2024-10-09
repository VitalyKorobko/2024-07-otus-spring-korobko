package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDto {
    private long id;

    private String text;

    private long bookId;

}
