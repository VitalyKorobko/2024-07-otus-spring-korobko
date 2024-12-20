package ru.otus.hw.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public record Article(@Id String id, String title, Writer writer, List<String> text) {
}
