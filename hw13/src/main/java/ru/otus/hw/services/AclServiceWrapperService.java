package ru.otus.hw.services;

import org.springframework.security.acls.model.Permission;
import ru.otus.hw.models.User;

public interface AclServiceWrapperService {
    void createPermissions(Object object, Permission ... permissions);

    void createPermissionsByUser(User user);

}