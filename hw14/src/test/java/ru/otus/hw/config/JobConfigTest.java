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
                .contains(expectedBookMongoDto(1),
                        expectedBookMongoDto(2), expectedBookMongoDto(3));

        assertThat(getComments())
                .isNotEmpty()
                .hasSize(3)
                .contains(expectedCommentMongoDto(1),
                        expectedCommentMongoDto(2), expectedCommentMongoDto(3));
    }

    private CommentMongoDto expectedCommentMongoDto(int num) {
        if (num == 1) {
            return new CommentMongoDto(
                    "",
                    "Comment_1",
                    expectedBookMongoDto(1)
            );
        } else if (num == 2) {
            return new CommentMongoDto(
                    "",
                    "Comment_2",
                    expectedBookMongoDto(1)
            );
        } else if (num == 3) {
            return new CommentMongoDto(
                    "",
                    "Comment_2",
                    expectedBookMongoDto(1)
            );
        }
        return null;
    }

    private List<CommentMongoDto> getComments() {
        return mongoCommentRepository.findAll().stream()
                .peek(c -> {
                    c.setId("");
                    c.getBookMongoDto().setId("");
                    c.getBookMongoDto().setId("");
                    c.getBookMongoDto().getAuthorMongoDto().setId("");
                    c.getBookMongoDto().getGenreMongoDtoList().forEach(g -> g.setId(""));
                    c.getBookMongoDto().getGenreMongoDtoList().sort((o1, o2) ->
                            String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));

                })
                .toList();
    }

    private BookMongoDto expectedBookMongoDto(int num) {
        if (num == 1) {
            return new BookMongoDto(
                    "",
                    "BookTitle_1",
                    new AuthorMongoDto("", "Author_1"),
                    List.of(new GenreMongoDto("", "Genre_1"), new GenreMongoDto("", "Genre_2"))
            );
        } else if (num == 2) {
            return new BookMongoDto(
                    "",
                    "BookTitle_2",
                    new AuthorMongoDto("", "Author_2"),
                    List.of(new GenreMongoDto("", "Genre_3"), new GenreMongoDto("", "Genre_4"))
            );
        } else if (num == 3) {
            return new BookMongoDto(
                    "",
                    "BookTitle_3",
                    new AuthorMongoDto("", "Author_3"),
                    List.of(new GenreMongoDto("", "Genre_5"), new GenreMongoDto("", "Genre_6"))
            );
        }
        return null;
    }

    private List<BookMongoDto> getBooks() {
        return mongoBookRepository.findAll().stream()
                .peek(b -> {
                    b.setId("");
                    b.getAuthorMongoDto().setId("");
                    b.getGenreMongoDtoList().forEach(g -> g.setId(""));
                    b.getGenreMongoDtoList().sort((o1, o2) ->
                            String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));
                })
                .toList();
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
