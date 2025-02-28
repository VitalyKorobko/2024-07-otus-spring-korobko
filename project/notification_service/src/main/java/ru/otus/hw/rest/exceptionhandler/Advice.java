package ru.otus.hw.rest.exceptionhandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.otus.hw.exception.NotAvailableException;

@ControllerAdvice
public class Advice {

    @ExceptionHandler(NotAvailableException.class)
    public ResponseEntity<ErrorResponse> tokenWasNotSent(NotAvailableException exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("service not available: " + exception.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> responseNotFound(RuntimeException exception) {
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("Server error! The request could not be completed. %s"
                        .formatted(exception.getMessage())));
    }

}
