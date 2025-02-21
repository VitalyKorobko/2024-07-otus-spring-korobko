package ru.otus.hw.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.UserDtoJs;
import ru.otus.hw.enums.RoleStorage;
import ru.otus.hw.model.User;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class UserDetailsController {
    private final UserDetailsService userDetailsService;

    @GetMapping("/api/v1/userdetails/{username}")
    public UserDetails get(@PathVariable String username) {
        System.out.println("\n=============controller userdetails==START===========\n");
        UserDetails user = userDetailsService.loadUserByUsername(username);
        System.out.println(user);
        System.out.println("\n=============controller userdetails==END===========\n");

        return user;
    }

}
