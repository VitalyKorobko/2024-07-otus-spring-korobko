package ru.otus.hw.commands;

import lombok.AllArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.services.ArticleService;
import ru.otus.hw.services.NewsPaperService;
import ru.otus.hw.services.WriterService;


import static java.lang.System.out;

@AllArgsConstructor
@ShellComponent
public class NewsPaperCommand {
    private final WriterService writerService;

    private final NewsPaperService newsPaperService;

    private final ArticleService articleService;

    @ShellMethod(value = "add NewsPapers: add <countNewsPapers>", key = "add")
    public void addNewsPapers(int countNewsPapers) {
        writerService.startWriterLoop(countNewsPapers);
    }

    @ShellMethod(value = "show all NewsPapers", key = "all")
    public void printAllNewsPapers() {
        newsPaperService.findAll().forEach((out::println));
    }

    @ShellMethod(value = "show all Articles", key = "art")
    public void printAllArticles() {
        articleService.findAll().forEach((out::println));
    }

}
