package ru.otus.hw.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.dto.BookMongoDto;
import ru.otus.hw.dto.CommentMongoDto;
import ru.otus.hw.dto.AuthorMongoDto;
import ru.otus.hw.dto.GenreMongoDto;
import ru.otus.hw.repositories.MongoAuthorRepository;
import ru.otus.hw.repositories.MongoBookRepository;
import ru.otus.hw.repositories.MongoCommentRepository;
import ru.otus.hw.repositories.MongoGenreRepository;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@SpringBatchTest
class JobConfigTest {
    private static final String JOB_NAME = "MigrationToMongo";

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private MongoAuthorRepository mongoAuthorRepository;

    @Autowired
    private MongoGenreRepository mongoGenreRepository;

    @Autowired
    private MongoCommentRepository mongoCommentRepository;

    @Autowired
    private MongoBookRepository mongoBookRepository;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testJob() throws Exception {
        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull()
                .extracting(Job::getName)
                .isEqualTo(JOB_NAME);

        JobParameters parameters = new JobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);

        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        assertThat(getAuthorsFullNames())
                .isNotEmpty()
                .hasSize(3)
                .contains("Author_1", "Author_2", "Author_3");

        assertThat(getGenresName())
                .isNotEmpty()
                .hasSize(6)
                .contains("Genre_1", "Genre_2", "Genre_3", "Genre_4", "Genre_5", "Genre_6");

        assertThat(getBooks())
                .isNotEmpty()
                .hasSize(3)
                .allMatch(bookMongoDto -> Objects.nonNull(bookMongoDto.getId()))
                .anyMatch(bookMongoDto -> Objects.equals(bookMongoDto.getTitle(), "BookTitle_1")
                        && Objects.equals(bookMongoDto.getAuthorMongoDto().getFullName(), "Author_1")
                        && bookMongoDto.getGenreMongoDtoList().stream().map(GenreMongoDto::getName).toList()
                        .containsAll(List.of("Genre_1", "Genre_2"))
                )
                .anyMatch(bookMongoDto -> Objects.equals(bookMongoDto.getTitle(), "BookTitle_2")
                        && Objects.equals(bookMongoDto.getAuthorMongoDto().getFullName(), "Author_2")
                        && bookMongoDto.getGenreMongoDtoList().stream().map(GenreMongoDto::getName).toList()
                        .containsAll(List.of("Genre_3", "Genre_4"))
                )
                .anyMatch(bookMongoDto -> Objects.equals(bookMongoDto.getTitle(), "BookTitle_3")
                        && Objects.equals(bookMongoDto.getAuthorMongoDto().getFullName(), "Author_3")
                        && bookMongoDto.getGenreMongoDtoList().stream().map(GenreMongoDto::getName).toList()
                        .containsAll(List.of("Genre_5", "Genre_6"))
                );

        assertThat(getComments())
                .isNotEmpty()
                .hasSize(3)
                .allMatch(commentMongoDto -> Objects.nonNull(commentMongoDto.getId()))
                .anyMatch(commentMongoDto -> Objects.equals(commentMongoDto.getText(), "Comment_1")
                        && Objects.equals(commentMongoDto.getBookMongoDto().getTitle(), "BookTitle_1")
                )
                .anyMatch(commentMongoDto -> Objects.equals(commentMongoDto.getText(), "Comment_2")
                        && Objects.equals(commentMongoDto.getBookMongoDto().getTitle(), "BookTitle_1")
                )
                .anyMatch(commentMongoDto -> Objects.equals(commentMongoDto.getText(), "Comment_3")
                        && Objects.equals(commentMongoDto.getBookMongoDto().getTitle(), "BookTitle_1")
                );
    }

    private List<CommentMongoDto> getComments() {
        return mongoCommentRepository.findAll();
    }

    private List<BookMongoDto> getBooks() {
        return mongoBookRepository.findAll();
    }

    private List<String> getGenresName() {
        return mongoGenreRepository.findAll().stream()
                .map(GenreMongoDto::getName)
                .toList();
    }

    private List<String> getAuthorsFullNames() {
        return mongoAuthorRepository.findAll().stream()
                .map(AuthorMongoDto::getFullName)
                .toList();
    }

}
