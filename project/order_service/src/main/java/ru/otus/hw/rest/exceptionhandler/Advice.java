package ru.otus.hw.rest.exceptionhandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.ParseDateException;

@ControllerAdvice
public class Advice {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> entityNotFound(EntityNotFoundException exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Не найдено! " + exception.getMessage()));
    }

    @ExceptionHandler(ParseDateException.class)
    public ResponseEntity<ErrorResponse> canNotParse(ParseDateException exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> responseNotFound(RuntimeException exception) {
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("Server error! The request could not be completed. " + exception.getMessage()));
    }

}
