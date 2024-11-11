package ru.otus.hw.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/api/v1/comment")
    public List<CommentDto> getCommentsByBook(@RequestParam("book_id") long bookId) {
        return commentService.findAllCommentsByBookId(bookId);
    }

    @GetMapping("/api/v1/comment/{id}")
    public CommentDto getComment(@PathVariable("id") long id) {
        return commentService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с id = %d не найден".formatted(id)));
    }

    @PatchMapping("/api/v1/comment")
    public CommentDto updateComment(@RequestBody CommentDto commentDto) {
        return commentService.update(commentDto.getId(), commentDto.getText(), commentDto.getBookId());
    }

    @PostMapping("/api/v1/comment/new")
    public CommentDto addComment(@RequestBody CommentDto commentDto) {
        return commentService.insert(commentDto.getText(), commentDto.getBookId());

    }

    @DeleteMapping("/api/v1/comment/{id}")
    public void deleteComment(@PathVariable long id) {
        commentService.deleteById(id);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> commentNotFound(EntityNotFoundException exception) {
        return ResponseEntity.badRequest().body("Ошибка! " + exception.getMessage());
    }


}
