package ru.otus.hw.services;

import ru.otus.hw.domain.Writer;

public interface WriterService {

    Writer create(String writerName);

    void startWriterLoop(int countNewsPapers);


}
