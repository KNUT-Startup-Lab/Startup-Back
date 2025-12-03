package com.startup.campusmate.domain.point.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PointSummaryResponse {
    private String residentId;
    private String residentName;
    private String roomNumber;
    private int totalMerit;      // 총 상점
    private int totalDemerit;    // 총 벌점
    private int netPoints;       // 순점수 (상점 - 벌점)
    private String message;
}