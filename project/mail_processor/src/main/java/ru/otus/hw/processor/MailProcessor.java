package ru.otus.hw.processor;

public interface MailProcessor<T> {

    T process(T data);
}
