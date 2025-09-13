package com.startup.campusmate.domain.resident.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResidentDetailResponse {
    private ResidentDto residentInfo;
    private String message;
}