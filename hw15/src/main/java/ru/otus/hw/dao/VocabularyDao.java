package ru.otus.hw.dao;

import java.util.List;

public interface VocabularyDao {
    List<String> findAll(String vocabularyPath);
}
