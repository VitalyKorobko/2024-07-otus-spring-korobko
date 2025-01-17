package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.dto.AuthorMongoDto;
import ru.otus.hw.dto.BookMongoDto;
import ru.otus.hw.dto.GenreMongoDto;
import java.util.List;
import java.util.Set;

public interface MongoBookRepository extends MongoRepository<BookMongoDto, String> {

    List<BookMongoDto> findByTitleAndAuthorMongoDtoAndGenreMongoDtoListIn(
            String title, AuthorMongoDto authorMongoDto, Set<GenreMongoDto> genreMongoDtos);


}
