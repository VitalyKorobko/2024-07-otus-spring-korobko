package ru.otus.hw.services;

import ru.otus.hw.dto.RoleDto;
import ru.otus.hw.models.Role;

import java.util.List;

public interface RoleService {

    List<RoleDto> findAll();

}
