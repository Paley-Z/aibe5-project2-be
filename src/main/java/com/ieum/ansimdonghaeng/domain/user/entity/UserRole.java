package com.ieum.ansimdonghaeng.domain.user.entity;

public enum UserRole {
    USER("ROLE_USER"),
    FREELANCER("ROLE_FREELANCER"),
    ADMIN("ROLE_ADMIN");

    private final String code;

    UserRole(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String asAuthority() {
        return code;
    }

    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role code: " + code);
    }
}
