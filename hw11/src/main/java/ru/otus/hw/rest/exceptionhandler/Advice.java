package ru.otus.hw.rest.exceptionhandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import ru.otus.hw.exceptions.EntityNotFoundException;

@ControllerAdvice
public class Advice {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> entityNotFound(EntityNotFoundException exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Не найдено! " + exception.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> responseNotFound(RuntimeException exception) {
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("Server error! The request could not be completed."));
    }

}
