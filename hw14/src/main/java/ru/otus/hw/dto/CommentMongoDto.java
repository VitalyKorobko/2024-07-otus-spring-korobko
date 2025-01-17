package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentMongoDto {
    private String id;

    private String text;

    private BookMongoDto bookMongoDto;

}
