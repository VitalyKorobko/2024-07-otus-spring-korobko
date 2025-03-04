package ru.otus.hw.exception;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException() {
    }

    public ServiceNotFoundException(String message) {
        super(message);
    }
}
