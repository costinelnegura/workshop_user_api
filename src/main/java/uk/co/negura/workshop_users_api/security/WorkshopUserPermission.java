package uk.co.negura.workshop_users_api.security;

public enum WorkshopUserPermission {
    ESTIMATOR_READ("estimator:read"),
    ESTIMATOR_WRITE("estimator:write"),
    PROJECT_READ("project:read"),
    PROJECT_WRITE("project:write"),
    ADMIN_READ("admin:read"),
    ADMIN_WRITE("admin:write");

    private final String permission;

    WorkshopUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
