package ru.otus.hw.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
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
import ru.otus.hw.exceptions.NotAvailableException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.services.CommentService;

import java.util.List;

@Slf4j
@RestController
public class CommentController {
    private static final String NOT_AVAILABLE_MESSAGE = "service not available";

    private final CommentService commentService;

    private final CommentMapper commentMapper;

    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;


    public CommentController(CommentService commentService, CommentMapper commentMapper,
                             Resilience4JCircuitBreakerFactory circuitBreakerFactory) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @GetMapping(value = "/api/v1/comment")
    public List<CommentDto> getCommentsByBook(@RequestParam("book_id") long bookId) {
        return circuitBreakerFactory.create("getCommentsByBookCircuitBreaker").run(
                () -> commentService.findAllCommentsByBookId(bookId),
                throwable -> circuitBreakerFallBackToGetComments(bookId, throwable)
        );
    }

    @GetMapping("/api/v1/comment/{id}")
    public CommentDto getComment(@PathVariable("id") long id) {
        return circuitBreakerFactory.create("getCommentCircuitBreaker").run(
                () -> commentService.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Комментарий с id = %d не найден".formatted(id))),
                throwable -> circuitBreakerFallBackToGetComment(id, throwable)
        );
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
                circuitBreakerFactory.create("updateCommentCircuitBreaker").run(
                        () -> commentService.update(id, commentDtoWeb.getText(), commentDtoWeb.getBookId()),
                        throwable -> circuitBreakerFallBackToUpdateComment(commentDtoWeb, throwable)
                ),
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
                circuitBreakerFactory.create("createCommentCircuitBreakerFactory").run(
                        () -> commentService.insert(commentDtoWeb.getText(), commentDtoWeb.getBookId()),
                        throwable -> circuitBreakerFallBackToCreateComment(commentDtoWeb, throwable)
                ),
                commentDtoWeb.getMessage()
        );
    }


    @DeleteMapping("/api/v1/comment/{id}")
    public void deleteComment(@PathVariable long id) {
        commentService.deleteById(id);
    }

    private List<CommentDto> circuitBreakerFallBackToGetComments(long bookId, Throwable e) {
        log.error("circuit breaker got open state when receiving comments with book_id={}: Err: {}:{}",
                bookId, e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

    private CommentDto circuitBreakerFallBackToGetComment(long id, Throwable e) {
        log.error("circuit breaker got open state when receiving comment with id={}: Err: {}:{}",
                id, e, e.getMessage());
        if (e.getClass() == EntityNotFoundException.class) {
            throw new EntityNotFoundException(e.getMessage());
        } else {
            throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
        }
    }

    private CommentDto circuitBreakerFallBackToCreateComment(CommentDtoWeb commentDtoWeb, Throwable e) {
        log.error("circuit breaker got open state when creating new comment: {}. Err: {}:{}",
                commentDtoWeb, e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

    private CommentDto circuitBreakerFallBackToUpdateComment(CommentDtoWeb commentDtoWeb, Throwable e) {
        log.error("circuit breaker got open state when updating comment: {}. Err: {}:{}",
                commentDtoWeb, e, e.getMessage());
        throw new NotAvailableException(NOT_AVAILABLE_MESSAGE);
    }

}
