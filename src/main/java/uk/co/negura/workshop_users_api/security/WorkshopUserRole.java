package uk.co.negura.workshop_users_api.security;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum WorkshopUserRole {
    ESTIMATOR(Sets.newHashSet()),
    ADMIN(Sets.newHashSet(
            WorkshopUserPermission.PROJECT_READ,
            WorkshopUserPermission.PROJECT_WRITE,
            WorkshopUserPermission.ESTIMATOR_READ,
            WorkshopUserPermission.ESTIMATOR_WRITE,
            WorkshopUserPermission.ADMIN_READ,
            WorkshopUserPermission.ADMIN_WRITE)),
    ESTIMATORTRAINEE(Sets.newHashSet(
            WorkshopUserPermission.PROJECT_READ,
            WorkshopUserPermission.ESTIMATOR_READ));

    private final Set<WorkshopUserPermission> permission;

    WorkshopUserRole(Set<WorkshopUserPermission> permissions) {
        this.permission = permissions;
    }

    public Set<WorkshopUserPermission> getPermission(){
        return permission;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthoritySet(){
        Set<SimpleGrantedAuthority> permissions = getPermission().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());

        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return permissions;
    }

}
