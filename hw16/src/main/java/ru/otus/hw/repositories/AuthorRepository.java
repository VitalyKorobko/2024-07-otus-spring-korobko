package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Author;

import java.util.List;

@RepositoryRestResource(path = "auth")
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @RestResource(path = "names")
//    @RestResource
    List<Author> findByFullName(String name);

}
