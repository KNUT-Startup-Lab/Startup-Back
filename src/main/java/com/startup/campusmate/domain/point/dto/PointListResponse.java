package com.startup.campusmate.domain.point.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PointListResponse {
    private List<PointDto> points;
    private long totalCount;
    private String message;
}