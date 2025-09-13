package com.startup.campusmate.domain.resident.repository;

import com.startup.campusmate.domain.resident.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {
    boolean existsByRoomNumber(String roomNumber);
    boolean existsByStudentId(String studentId);
    Optional<Resident> findByResidentId(String residentId);
}