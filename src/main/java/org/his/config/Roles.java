package org.his.config;

public enum Roles {
    ADMIN,
    DOCTOR,
    NURSE,
    RECEPTIONIST,
    PHARMACIST;

    public static boolean isValidRole(String role) {
        for (Roles r : Roles.values()) {
            if (r.name().equals(role.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
