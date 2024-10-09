package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentDtoConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class CommentCommands {
    private final CommentService commentService;

    private final CommentDtoConverter commentDtoConverter;

    @ShellMethod(value = "Find comments by BookId", key = "cbbid")
    public String findAllCommentsByBookId(long bookId) {
        return commentService.findAllCommentsByBookId(bookId).stream()
                .map(commentDtoConverter::commentDtoToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find comment by Id", key = "cbid")
    public String findCommentById(long id) {
        return commentService.findById(id)
                .map(commentDtoConverter::commentDtoToString)
                .orElse("comment with id = %d not exist".formatted(id));
    }

    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertComment(String text, long bookId) {
        var savedComment = commentService.insert(text, bookId);
        return commentDtoConverter.commentDtoToString(savedComment);
    }

    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(long id, String text, long bookId) {
        return commentDtoConverter.commentDtoToString(commentService.update(id, text, bookId));
    }

    @ShellMethod(value = "Delete comment", key = "cdel")
    public void deleteComment(long id) {
        commentService.deleteById(id);
    }

}
