package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentMongoDto;

@Component
public class CommentMongoDtoConverter {
    public String commentDtoToString(CommentMongoDto commentMongoDto) {
        return "Id: %s, Comment: %s, BookId: %s"
                .formatted(commentMongoDto.getId(), commentMongoDto.getText(),
                        commentMongoDto.getBookMongoDto().getId());
    }

}
