package ru.otus.hw.exception;

public class NotAvailableException extends RuntimeException {
    public NotAvailableException() {
    }

    public NotAvailableException(String message) {
        super(message);
    }
}
