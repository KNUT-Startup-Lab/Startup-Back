package com.startup.campusmate.domain.resident.point.entity;

import com.startup.campusmate.domain.resident.resident.entity.Resident;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "points")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "point_id", unique = true)
    private String pointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", referencedColumnName = "resident_id", nullable = false)
    private Resident resident;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_type", nullable = false)
    private PointType pointType;  // MERIT(상점) / DEMERIT(벌점)

    @Column(name = "points", nullable = false)
    private Integer points;  // 점수 (양수)

    @Column(name = "reason", nullable = false)
    private String reason;  // 사유

    @Column(name = "category")
    private String category;  // 분류 (청소, 소음, 봉사활동 등)

    @Column(name = "issued_date", nullable = false)
    private String issuedDate;  // 부여일

    @Column(name = "issued_by")
    private String issuedBy;  // 부여자 (관리자 ID 또는 이름)

    @Column(name = "note")
    private String note;  // 비고

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (pointId == null) {
            pointId = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}