package ru.otus.hw.exceptions;

public class NotAvailableException extends RuntimeException {
    public NotAvailableException() {
    }

    public NotAvailableException(String message) {
        super(message);
    }
}
