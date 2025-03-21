package ru.otus.hw.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.model.Role;
import ru.otus.hw.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(value = "roles-entity-graph")
    Optional<User> findByUsername(String username);

    @EntityGraph(value = "roles-entity-graph")
    Optional<User> findByEmail(String email);

    @EntityGraph(value = "roles-entity-graph")
    List<User> findAllByRoles(Set<Role> roles);

}
