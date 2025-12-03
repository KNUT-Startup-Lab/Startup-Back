package com.startup.campusmate.domain.point.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PointUpdateRequest {
    @NotBlank(message = "상벌점 유형은 필수입니다")
    private String pointType;

    @NotNull(message = "점수는 필수입니다")
    @Min(value = 1, message = "점수는 1점 이상이어야 합니다")
    private Integer points;

    @NotBlank(message = "사유는 필수입니다")
    private String reason;

    private String category;

    @NotBlank(message = "부여일은 필수입니다")
    private String issuedDate;

    private String issuedBy;
    private String note;
}