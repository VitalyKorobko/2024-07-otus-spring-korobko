package ru.otus.hw.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.dto.AuthorMongoDto;
import ru.otus.hw.dto.BookMongoDto;
import ru.otus.hw.dto.CommentMongoDto;
import ru.otus.hw.dto.GenreMongoDto;
import ru.otus.hw.mapper.AuthorMapper;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;


@Configuration
public class JobConfig {
    private static final String JOB_NAME = "MigrationToMongo";

    private static final String AUTHOR_STEP_NAME = "transformAuthorStep";

    private static final String AUTHOR_READER_NAME = "authorItemReader";

    private static final String GENRE_STEP_NAME = "transformGenreStep";

    private static final String GENRE_READER_NAME = "genreItemReader";

    private static final String BOOK_STEP_NAME = "transformBookStep";

    private static final String BOOK_READER_NAME = "bookItemReader";

    private static final String COMMENT_STEP_NAME = "transformCommentStep";

    private static final String COMMENT_READER_NAME = "commentItemReader";

    private static final int CHUNK_SIZE = 5;

    private static final int PAGE_SIZE = 5;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    public JobConfig(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Bean
    public Job toMongoMigrateJob(
            Step transformGenreStep,
            Step transformAuthorStep,
            Step transformBookStep,
            Step transformCommentStep
    ) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(transformAuthorStep)
                .next(transformGenreStep)
                .next(transformBookStep)
                .next(transformCommentStep)
                .build();
    }

    @Bean
    public Step transformAuthorStep(ItemReader<Author> authorReader, MongoItemWriter<AuthorMongoDto> authorWriter,
                                    ItemProcessor<Author, AuthorMongoDto> authorProcessor) {
        return new StepBuilder(AUTHOR_STEP_NAME, jobRepository)
                .<Author, AuthorMongoDto>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(authorReader)
                .processor(authorProcessor)
                .writer(authorWriter)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }


    @Bean
    public ItemProcessor<Author, AuthorMongoDto> authorProcessor(@NonNull AuthorMapper authorMapper) {
        return authorMapper::toDto;
    }

    @Bean
    public JpaPagingItemReader<Author> authorReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Author>()
                .name(AUTHOR_READER_NAME)
                .entityManagerFactory(emf)
                .queryString("select a from Author a")
                .pageSize(PAGE_SIZE)
                .build();
    }

    @Bean
    public MongoItemWriter<AuthorMongoDto> authorWriter(MongoDatabaseFactory mongoDatabaseFactory) {
        MongoItemWriter<AuthorMongoDto> writer = new MongoItemWriter<>();
        writer.setTemplate(new MongoTemplate(mongoDatabaseFactory));
        return writer;
    }

    @Bean
    public Step transformGenreStep(ItemReader<Genre> genreReader, MongoItemWriter<GenreMongoDto> genreWriter,
                                   ItemProcessor<Genre, GenreMongoDto> genreProcessor) {
        return new StepBuilder(GENRE_STEP_NAME, jobRepository)
                .<Genre, GenreMongoDto>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(genreReader)
                .processor(genreProcessor)
                .writer(genreWriter)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public ItemProcessor<Genre, GenreMongoDto> genreProcessor(@NonNull GenreMapper genreMapper) {
        return genreMapper::toDto;
    }


    @Bean
    public JpaPagingItemReader<Genre> genreReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Genre>()
                .name(GENRE_READER_NAME)
                .entityManagerFactory(emf)
                .queryString("select g from Genre g")
                .pageSize(PAGE_SIZE)
                .build();
    }

    @Bean
    public MongoItemWriter<GenreMongoDto> genreWriter(MongoDatabaseFactory mongoDatabaseFactory) {
        MongoItemWriter<GenreMongoDto> writer = new MongoItemWriter<>();
        writer.setTemplate(new MongoTemplate(mongoDatabaseFactory));
        return writer;
    }

    @Bean
    public Step transformBookStep(ItemReader<Book> bookReader, MongoItemWriter<BookMongoDto> bookWriter,
                                  ItemProcessor<Book, BookMongoDto> bookProcessor) {
        return new StepBuilder(BOOK_STEP_NAME, jobRepository)
                .<Book, BookMongoDto>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(bookReader)
                .processor(bookProcessor)
                .writer(bookWriter)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public ItemProcessor<Book, BookMongoDto> bookProcessor(@NonNull BookMapper bookMapper) {
        return bookMapper::toDto;
    }


    @Bean
    public JpaPagingItemReader<Book> bookReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Book>()
                .name(BOOK_READER_NAME)
                .entityManagerFactory(emf)
                .queryString("select b from Book b")
                .pageSize(PAGE_SIZE)
                .build();
    }

    @Bean
    public MongoItemWriter<BookMongoDto> bookWriter(MongoDatabaseFactory mongoDatabaseFactory) {
        MongoItemWriter<BookMongoDto> writer = new MongoItemWriter<>();
        writer.setTemplate(new MongoTemplate(mongoDatabaseFactory));
        return writer;
    }

    @Bean
    public Step transformCommentStep(ItemReader<Comment> commentReader, MongoItemWriter<CommentMongoDto> commentWriter,
                                     ItemProcessor<Comment, CommentMongoDto> commentProcessor) {
        return new StepBuilder(COMMENT_STEP_NAME, jobRepository)
                .<Comment, CommentMongoDto>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(commentReader)
                .processor(commentProcessor)
                .writer(commentWriter)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public ItemProcessor<Comment, CommentMongoDto> commentProcessor(@NonNull CommentMapper commentMapper) {
        return commentMapper::toDto;
    }


    @Bean
    public JpaPagingItemReader<Comment> commentReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Comment>()
                .name(COMMENT_READER_NAME)
                .entityManagerFactory(emf)
                .queryString("select c from Comment c")
                .pageSize(PAGE_SIZE)
                .build();
    }

    @Bean
    public MongoItemWriter<CommentMongoDto> commentWriter(MongoDatabaseFactory mongoDatabaseFactory) {
        MongoItemWriter<CommentMongoDto> writer = new MongoItemWriter<>();
        writer.setTemplate(new MongoTemplate(mongoDatabaseFactory));
        return writer;
    }

}
