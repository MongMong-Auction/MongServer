package com.mmserver.domain.EnumType;

/**
 * 사용자 권한 Enum
 */
public enum RoleType {
    /**
     * 관리자
     */
    ADMIN("ROLE_ADMIN"),
    /**
     * 판매자
     */
    MANAGER("ROLE_MANAGE"),
    /**
     * 사용자
     */
    USER("ROLE_USER");

    private final String value;

    RoleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
