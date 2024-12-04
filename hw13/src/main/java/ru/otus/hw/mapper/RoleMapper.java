package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.RoleDto;
import ru.otus.hw.models.Role;

@Component
public class RoleMapper {

    public Role toRole(RoleDto roleDto) {
        return new Role(roleDto.getId(), roleDto.getName());
    }

    public RoleDto toRoleDto(Role role) {
        return new RoleDto(role.getId(), role.getName());
    }

}
