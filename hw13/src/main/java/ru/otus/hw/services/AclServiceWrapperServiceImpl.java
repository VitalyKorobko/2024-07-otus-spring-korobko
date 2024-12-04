package ru.otus.hw.services;

import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.otus.hw.enums.RoleType;
import ru.otus.hw.models.User;

import static org.springframework.security.acls.domain.BasePermission.READ;
import static org.springframework.security.acls.domain.BasePermission.CREATE;
import static org.springframework.security.acls.domain.BasePermission.WRITE;
import static org.springframework.security.acls.domain.BasePermission.ADMINISTRATION;
import static org.springframework.security.acls.domain.BasePermission.DELETE;

@Service
public class AclServiceWrapperServiceImpl implements AclServiceWrapperService {
    private static final Sid ADMIN = new GrantedAuthoritySid(RoleType.ADMIN.getAuthority());

    private static final Sid USER = new GrantedAuthoritySid(RoleType.USER.getAuthority());

    private final MutableAclService mutableAclService;

    public AclServiceWrapperServiceImpl(MutableAclService mutableAclService) {
        this.mutableAclService = mutableAclService;
    }

    @Override
    public void createPermissions(Object object, Permission... permissions) {
        Sid owner = getOwner();
        MutableAcl acl = createAcl(object);
        addPermissions(owner, acl, READ, CREATE, WRITE, DELETE);
        addPermissions(ADMIN, acl, READ, CREATE, WRITE, DELETE, ADMINISTRATION);
        addPermissions(USER, acl, READ);
        mutableAclService.updateAcl(acl);
    }

    @Override
    public void createPermissionsByUser(User user) {
        MutableAcl acl = createAcl(user);
        addPermissions(ADMIN, acl, READ, CREATE, WRITE, DELETE, ADMINISTRATION);
        mutableAclService.updateAcl(acl);
    }

    private MutableAcl createAcl(Object object) {
        return mutableAclService.createAcl(new ObjectIdentityImpl(object));
    }

    private Sid getOwner() {
        return new PrincipalSid(SecurityContextHolder.getContext().getAuthentication());
    }

    private void addPermissions(Sid sid, MutableAcl acl, Permission... permissions) {
        for (Permission permission : permissions) {
            acl.insertAce(acl.getEntries().size(), permission, sid, true);
        }
    }

}
