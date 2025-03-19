package ru.otus.hw.model;

public interface DataForSending<T> {
    long id();

    T data();
}
