package com.startup.campusmate.domain.resident.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResidentListResponse {
    private List<ResidentDto> residents;
    private Long totalCount;
    private String message;
}
