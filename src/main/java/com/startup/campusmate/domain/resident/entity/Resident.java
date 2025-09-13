package com.startup.campusmate.domain.resident.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "residents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resident_id", unique = true)
    private String residentId;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "room_number", unique = true, nullable = false)
    private String roomNumber;

    @Column(name = "check_in_date", nullable = false)
    private String checkInDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (residentId == null) {
            residentId = UUID.randomUUID().toString();
        }
    }
}