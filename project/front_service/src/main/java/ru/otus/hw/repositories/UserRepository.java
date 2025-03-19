package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.Role;
import ru.otus.hw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(value = "roles-entity-graph")
    Optional<User> findByUsername(String username);

    @EntityGraph(value = "roles-entity-graph")
    @Override
    Optional<User> findById(Long id);

    @EntityGraph(value = "roles-entity-graph")
    List<User> findAllByRoles(Role role);

    @EntityGraph(value = "roles-entity-graph")
    Optional<User> findByEmail(String email);


}
