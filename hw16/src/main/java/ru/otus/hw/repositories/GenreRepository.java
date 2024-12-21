package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

@RepositoryRestResource(path = "genre")
public interface GenreRepository extends JpaRepository<Genre, Long> {

    List<Genre> findAllByIdIn(Set<Long> ids);

    @RestResource(path = "names")
    List<Genre> findAllByNameIn(Set<String> names);

}
