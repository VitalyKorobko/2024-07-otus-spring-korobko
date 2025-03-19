package ru.otus.hw.exceptions;

public class ParseDateException extends RuntimeException {
    public ParseDateException() {
    }

    public ParseDateException(String message) {
        super(message);
    }
}
