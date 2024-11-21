package ru.otus.hw.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.UserMapper;
import ru.otus.hw.models.Role;

import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с пользователями ")
@DataJpaTest
@Import({UserDetailsServiceImpl.class, UserMapper.class})
@Transactional(propagation = Propagation.NEVER)
public class UserServiceImplTest {
    private static final String PASSWORD = "$2y$13$H81FL5loLZN/s8baA6NcdOrXF1asdG4.tsf9no5a/UuVqrRtsy31i";

    @Autowired
    private UserDetailsServiceImpl service;

    @DisplayName(" должен возвращать корректный UserDetails по username")
    @Test
    void shouldReturnCorrectUserDetailsByUsername() {
        var userDetails = service.loadUserByUsername("user");
        assertThat(userDetails)
                .matches(u -> Objects.equals(u.getUsername(), "user"))
                .matches(u -> Objects.equals(u.getPassword(), PASSWORD))
                .matches(u -> Objects.equals(u.isEnabled(), true))
                .matches(u -> Objects.equals(u.getAuthorities(),
                        Set.of(new Role(1, "USER"), new Role(2, "ADMIN"))));
    }

    @DisplayName(" должен выбрасывать исключение при попытке получения несуществующего пользователя")
    @Test
    void shouldThrowExceptionIfUserNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> service.loadUserByUsername("someUser"));
    }


}
