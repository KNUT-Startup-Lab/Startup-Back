package com.startup.campusmate.domain.resident.resident.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResidentRegistrationResponse {
    private String residentId;
    private String message;
}