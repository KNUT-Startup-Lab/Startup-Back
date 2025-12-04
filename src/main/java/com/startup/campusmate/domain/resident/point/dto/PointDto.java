package com.startup.campusmate.domain.resident.point.dto;

import com.startup.campusmate.domain.resident.point.entity.Point;
import lombok.Data;

@Data
public class PointDto {
    private String pointId;
    private String residentId;
    private String residentName;
    private String roomNumber;
    private String pointType;
    private Integer points;
    private String reason;
    private String category;
    private String issuedDate;
    private String issuedBy;
    private String note;
    private String createdAt;

    public PointDto(Point point) {
        this.pointId = point.getPointId();
        this.residentId = point.getResident().getResidentId();
        this.residentName = point.getResident().getName();
        this.roomNumber = point.getResident().getRoomNumber();
        this.pointType = point.getPointType().name();
        this.points = point.getPoints();
        this.reason = point.getReason();
        this.category = point.getCategory();
        this.issuedDate = point.getIssuedDate();
        this.issuedBy = point.getIssuedBy();
        this.note = point.getNote();
        this.createdAt = point.getCreatedAt() != null
                ? point.getCreatedAt().toString() : null;
    }
}