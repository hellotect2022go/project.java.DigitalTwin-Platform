package com.mpole.hdt.digitaltwin.application.repository.entity;

public enum UserRole {
    ROLE_ADMIN("관리자"),
    ROLE_MANAGER("매니저"),
    ROLE_USER("일반 사용자"),
    ROLE_VIEWER("뷰어");
    
    private final String description;
    
    UserRole(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

