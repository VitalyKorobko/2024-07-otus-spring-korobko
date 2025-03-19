package ru.otus.hw.exception;

public class ImpossibleSaveEntityException extends RuntimeException {
    public ImpossibleSaveEntityException() {
    }

    public ImpossibleSaveEntityException(String message) {
        super(message);
    }
}
