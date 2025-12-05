package com.startup.campusmate.domain.push.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PushTokenRequest {

    // Setters
    // Getters
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

}