package com.startup.campusmate.domain.resident.resident.repository;

import com.startup.campusmate.domain.resident.resident.dto.ResidentStatus;
import com.startup.campusmate.domain.resident.resident.entity.Resident;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {
    boolean existsByRoomNumber(String roomNumber);
    boolean existsByRoomNumberAndResidentIdNot(String roomNumber, String residentId);
    boolean existsByStudentId(String studentId);
    Optional<Resident> findByResidentId(String residentId);
    boolean existsByResidentId(String residentId);
    // 검색 및 필터링을 위한 커스텀 쿼리
    @Query("SELECT r FROM Resident r WHERE " +
            "(:search IS NULL OR " +
            "LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.studentId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:status IS NULL OR r.status = :status)")
    Page<Resident> findResidentsWithFilter(
            @Param("search") String search,
            @Param("status") ResidentStatus status,
            Pageable pageable
    );
}