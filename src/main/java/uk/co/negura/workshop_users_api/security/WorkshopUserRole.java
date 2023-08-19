package uk.co.negura.workshop_users_api.security;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum WorkshopUserRole {
    STUDENT(Sets.newHashSet()),
    ADMIN(Sets.newHashSet(
            WorkshopUserPermission.COURSE_READ,
            WorkshopUserPermission.COURSE_WRITE,
            WorkshopUserPermission.STUDENT_READ,
            WorkshopUserPermission.STUDENT_WRITE)),
    ADMINTRAINEE(Sets.newHashSet(
            WorkshopUserPermission.COURSE_READ,
            WorkshopUserPermission.STUDENT_READ));

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
