package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Role;
import ru.otus.hw.models.User;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Репозиторий на основе Jpa для работы с сущностью пользователь ")
@DataJpaTest
public class JpaUserRepositoryTest {
    private static final String PASSWORD = "$2y$13$H81FL5loLZN/s8baA6NcdOrXF1asdG4.tsf9no5a/UuVqrRtsy31i";

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("должен возвращать корректного пользователя по Username")
    void shouldReturnCorrectUserByUsername() {
        var userOptional = userRepository.findByUsername("user");
        var expectedUser = new User(1, "user", PASSWORD, true,
                Set.of(new Role(1, "USER"), new Role(2, "ADMIN")));
        assertThat(userOptional).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedUser);

    }


}
