package ru.otus.hw.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentDtoWeb;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
public class CommentController {
    private final CommentService commentService;

    private final CommentMapper commentMapper;

    public CommentController(CommentService commentService, CommentMapper commentMapper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @GetMapping(value = "/api/v1/comment")
    public List<CommentDto> getCommentsByBook(@RequestParam("book_id") long bookId) {
        return commentService.findAllCommentsByBookId(bookId);
    }

    @GetMapping("/api/v1/comment/{id}")
    public CommentDto getComment(@PathVariable("id") long id) {
        return commentService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с id = %d не найден".formatted(id)));
    }

    @PatchMapping("/api/v1/comment/{id}")
    public CommentDtoWeb updateComment(@Valid @RequestBody CommentDtoWeb commentDtoWeb,
                                       BindingResult bindingResult, @PathVariable("id") long id) {
        if (bindingResult.hasErrors()) {
            return new CommentDtoWeb(
                    commentDtoWeb.getId(),
                    commentDtoWeb.getText(),
                    commentDtoWeb.getBookId(),
                    bindingResult.getFieldError().getDefaultMessage()
            );
        }
        return commentMapper.commentDtoWeb(
                commentService.update(id, commentDtoWeb.getText(), commentDtoWeb.getBookId()),
                commentDtoWeb.getMessage()
        );
    }

    @PostMapping("/api/v1/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDtoWeb addComment(@Valid @RequestBody CommentDtoWeb commentDtoWeb, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new CommentDtoWeb(
                    commentDtoWeb.getId(),
                    commentDtoWeb.getText(),
                    commentDtoWeb.getBookId(),
                    bindingResult.getFieldError().getDefaultMessage()
            );
        }
        return commentMapper.commentDtoWeb(
                commentService.insert(commentDtoWeb.getText(), commentDtoWeb.getBookId()),
                commentDtoWeb.getMessage()
        );

    }

    @DeleteMapping("/api/v1/comment/{id}")
    public void deleteComment(@PathVariable long id) {
        commentService.deleteById(id);
    }


}
