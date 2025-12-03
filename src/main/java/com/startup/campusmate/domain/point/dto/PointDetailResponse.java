package com.startup.campusmate.domain.point.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PointDetailResponse {
    private PointDto point;
    private String message;
}