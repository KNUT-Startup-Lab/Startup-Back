package com.startup.campusmate.domain.resident.resident.dto;

import com.startup.campusmate.domain.resident.resident.entity.Resident;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResidentDto {
    private String residentId;
    private String studentId;
    private String name;
    private String roomNumber;
    private String checkInDate;
    private String checkOutDate;
    private String status;
//    private String statusDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity to DTO 변환 생성자
    public ResidentDto(Resident resident) {
        this.residentId = resident.getResidentId();
        this.studentId = resident.getStudentId();
        this.name = resident.getName();
        this.roomNumber = resident.getRoomNumber();
        this.checkInDate = resident.getCheckInDate();
        this.checkOutDate = resident.getCheckOutDate();
        this.status = resident.getStatus().name();
//        this.statusDescription = resident.getStatus().getDescription();
        this.createdAt = resident.getCreatedAt();
        this.updatedAt = resident.getUpdatedAt();
    }
}