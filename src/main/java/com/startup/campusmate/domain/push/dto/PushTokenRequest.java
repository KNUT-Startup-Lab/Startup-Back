package com.startup.campusmate.domain.push.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PushTokenRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String expoPushToken;

    // 기본 생성자
    public PushTokenRequest() {}

    public PushTokenRequest(Long userId, String expoPushToken) {
        this.userId = userId;
        this.expoPushToken = expoPushToken;
    }

    // Getters
    public Long getUserId() {
        return userId;
    }

    public String getExpoPushToken() {
        return expoPushToken;
    }

    // Setters
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setExpoPushToken(String expoPushToken) {
        this.expoPushToken = expoPushToken;
    }
}