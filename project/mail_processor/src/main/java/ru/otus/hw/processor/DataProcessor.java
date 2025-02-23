package ru.otus.hw.processor;

public interface DataProcessor<T> {

    T process(T data);
}
