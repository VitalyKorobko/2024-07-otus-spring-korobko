package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.RoleDto;
import ru.otus.hw.mapper.RoleMapper;
import ru.otus.hw.repositories.RoleRepository;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService{

    private final RoleRepository repository;

    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository repository, RoleMapper roleMapper) {
        this.repository = repository;
        this.roleMapper = roleMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RoleDto> findAll() {
        return repository.findAll().stream()
                .map(roleMapper::toRoleDto).toList();
    }
}
