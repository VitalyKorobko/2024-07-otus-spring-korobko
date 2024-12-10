package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentDtoWeb;
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
                comment.getBook().getId()
        );
    }

    public CommentDtoWeb commentDtoWeb(CommentDto commentDto, String message) {
        return new CommentDtoWeb(
                commentDto.getId(),
                commentDto.getText(),
                commentDto.getBookId(),
                message
        );
    }

    public CommentDtoWeb commentDtoWeb(Comment comment, String message) {
        return new CommentDtoWeb(
                comment.getId(),
                comment.getText(),
                comment.getBook().getId(),
                message
        );
    }

}
