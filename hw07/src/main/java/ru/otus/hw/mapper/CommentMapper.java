package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

@Component
public class CommentMapper {

    public Comment toComment(CommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                new Book(commentDto.getBookId())
        );
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getBookId()
        );
    }

}
