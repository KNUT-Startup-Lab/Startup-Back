package com.startup.campusmate.domain.qna.entity;

public enum QnaStatus {
    PENDING("pending"),
    ANSWERED("answered");

    private final String value;

    QnaStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
