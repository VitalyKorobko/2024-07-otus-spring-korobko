package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;

@Component
public class CommentDtoConverter {
    public String commentDtoToString(CommentDto commentDto) {
        return "Id: %s, Comment: %s, BookId: %s"
                .formatted(commentDto.getId(), commentDto.getText(), commentDto.getBookId());
    }

}
