package com.startup.campusmate.domain.resident.point.repository;

import com.startup.campusmate.domain.resident.point.entity.Point;
import com.startup.campusmate.domain.resident.point.entity.PointType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByPointId(String pointId);

    // 특정 입주자의 상벌점 목록
    @Query("SELECT p FROM Point p WHERE p.resident.residentId = :residentId ORDER BY p.createdAt DESC")
    List<Point> findByResidentId(@Param("residentId") String residentId);

    // 필터링된 상벌점 목록 (페이징)
    @Query("SELECT p FROM Point p " +
            "WHERE (:residentId IS NULL OR p.resident.residentId = :residentId) " +
            "AND (:pointType IS NULL OR p.pointType = :pointType) " +
            "AND (:category IS NULL OR p.category = :category) " +
            "AND (:search IS NULL OR p.reason LIKE %:search% OR p.resident.name LIKE %:search%)")
    Page<Point> findPointsWithFilter(
            @Param("residentId") String residentId,
            @Param("pointType") PointType pointType,
            @Param("category") String category,
            @Param("search") String search,
            Pageable pageable);

    // 특정 입주자의 총 상점 합계
    @Query("SELECT COALESCE(SUM(p.points), 0) FROM Point p " +
            "WHERE p.resident.residentId = :residentId AND p.pointType = 'MERIT'")
    int sumMeritPointsByResidentId(@Param("residentId") String residentId);

    // 특정 입주자의 총 벌점 합계
    @Query("SELECT COALESCE(SUM(p.points), 0) FROM Point p " +
            "WHERE p.resident.residentId = :residentId AND p.pointType = 'DEMERIT'")
    int sumDemeritPointsByResidentId(@Param("residentId") String residentId);

    // 특정 카테고리별 상벌점 목록
    List<Point> findByCategory(String category);

    // 특정 기간의 상벌점 목록
    @Query("SELECT p FROM Point p WHERE p.issuedDate BETWEEN :startDate AND :endDate ORDER BY p.issuedDate DESC")
    List<Point> findByIssuedDateBetween(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}