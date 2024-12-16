package ru.otus.hw.exception;

public class VocabularyReadException extends RuntimeException{

    public VocabularyReadException() {
        super();
    }

    public VocabularyReadException(String message) {
        super(message);
    }

    public VocabularyReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
