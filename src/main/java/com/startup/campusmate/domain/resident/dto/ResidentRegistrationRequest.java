package com.startup.campusmate.domain.resident.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResidentRegistrationRequest {
    @NotBlank(message = "학번은 필수입니다")
    private String studentId;

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotBlank(message = "방 번호는 필수입니다")
    private String roomNumber;

    @NotBlank(message = "핸드폰 번호는 필수입니다")
    private String Phone;

    @NotBlank(message = "이메일은 필수입니다")
    private String Email;

    @NotBlank(message = "입주 날짜는 필수입니다")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "날짜 형식은 YYYY-MM-DD여야 합니다")
    private String checkInDate;
}
