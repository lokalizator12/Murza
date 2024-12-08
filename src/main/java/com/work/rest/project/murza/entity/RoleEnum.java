package com.work.rest.project.murza.entity;


import lombok.Getter;

@Getter
public enum RoleEnum {
    USER("Default user role (client)"),
    ADMIN("Default administrator role (moderator/manager)"),
    SUPER_ADMIN("Super-admin role (CEO | main-developer)");

    private final String description;

    RoleEnum(String description) {
        this.description = description;
    }
}
