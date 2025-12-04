package com.startup.campusmate.domain.resident.resident.dto;

public enum ResidentStatus {
    ACTIVE("입주중"),
    INACTIVE("퇴실"),
    PENDING("입주대기");

    private final String description;

    ResidentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}