package ru.otus.hw.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public record Writer(@Id String id, String name, String[] vocabulary) {

    @Override
    public String toString() {
        return "Writer{" +
                "name='" + name + '\'' +
                '}';
    }
}
