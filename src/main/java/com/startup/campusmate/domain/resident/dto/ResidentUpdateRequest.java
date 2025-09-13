package com.startup.campusmate.domain.resident.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResidentUpdateRequest {
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotBlank(message = "방 번호는 필수입니다")
    private String roomNumber;

    private String phone;
    private String email;
}