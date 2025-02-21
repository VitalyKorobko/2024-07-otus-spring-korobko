package ru.otus.hw.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.RoleDto;
import ru.otus.hw.dto.UserDtoWeb;
import ru.otus.hw.mapper.UserMapper;
import ru.otus.hw.model.Role;
import ru.otus.hw.model.User;
//import ru.otus.hw.repository.RoleRepository;
import ru.otus.hw.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

//    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    //todo Filter by username from Bearer token
    //todo RequestParam

    @GetMapping("/api/v1/user/username/{username}")
    public User getByUsername(@PathVariable("username") String username) {
        return userService.findByUserName(username);
    }

    @GetMapping("/api/v1/user/email/{email}")
    public User getByEmail(@PathVariable("email") String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/api/v1/user/id/{id}")
    public User getById(@PathVariable("id") long id) {
        return userService.findById(id);
    }

    @PostMapping("/api/v1/users")
    public List<User> findAllByRoles(@RequestBody Set<RoleDto> dtoSet) {
        var roles = dtoSet.stream()
                .map(roleDto -> Role.valueOf(roleDto.getRoleName())).collect(Collectors.toSet());
        return userService.findAllByRoles(roles);
    }

    @PostMapping("/api/v1/user")
    public UserDtoWeb create(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new UserDtoWeb(
                    0,
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.isEnabled(),
                    user.getRoles(),
                    bindingResult.getFieldError().getDefaultMessage()
            );
        }
        return userMapper.toUserDtoWeb(
                userService.update(
                        0,
                        user.getUsername(),
                        user.getPassword(),
                        user.getEmail(),
                        user.isEnabled(),
                        user.getRoles()
                ),
                null
        );
    }

    @PatchMapping("/api/v1/user")
    public UserDtoWeb update(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new UserDtoWeb(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.isEnabled(),
                    user.getRoles(),
                    bindingResult.getFieldError().getDefaultMessage()
            );
        }
        return userMapper.toUserDtoWeb(
                userService.insert(
                        user.getUsername(),
                        user.getPassword(),
                        user.getEmail(),
                        user.isEnabled(),
                        user.getRoles()
                ),
                null
        );
    }



}
