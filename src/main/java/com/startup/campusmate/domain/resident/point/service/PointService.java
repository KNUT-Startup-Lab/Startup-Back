package com.startup.campusmate.domain.resident.point.service;

import com.startup.campusmate.domain.point.dto.*;
import com.startup.campusmate.domain.resident.point.dto.*;
import com.startup.campusmate.domain.resident.resident.point.dto.*;
import com.startup.campusmate.domain.resident.point.entity.Point;
import com.startup.campusmate.domain.resident.point.entity.PointType;
import com.startup.campusmate.domain.resident.point.repository.PointRepository;
import com.startup.campusmate.domain.resident.resident.entity.Resident;
import com.startup.campusmate.domain.resident.resident.repository.ResidentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class PointService {

    private final PointRepository pointRepository;
    private final ResidentRepository residentRepository;

    public PointService(PointRepository pointRepository, ResidentRepository residentRepository) {
        this.pointRepository = pointRepository;
        this.residentRepository = residentRepository;
    }

    // 상벌점 부여
    public PointDetailResponse createPoint(PointCreateRequest request) {
        log.info("상벌점 부여 요청: residentId={}, type={}, points={}",
                request.getResidentId(), request.getPointType(), request.getPoints());

        // 입주자 존재 확인
        Resident resident = residentRepository.findByResidentId(request.getResidentId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "입주자를 찾을 수 없습니다: " + request.getResidentId()));

        // 상벌점 유형 변환
        PointType pointType = parsePointType(request.getPointType());

        // 날짜 유효성 검증
        validateDate(request.getIssuedDate());

        // Point 엔티티 생성
        Point point = new Point();
        point.setResident(resident);
        point.setPointType(pointType);
        point.setPoints(request.getPoints());
        point.setReason(request.getReason());
        point.setCategory(request.getCategory());
        point.setIssuedDate(request.getIssuedDate());
        point.setIssuedBy(request.getIssuedBy());
        point.setNote(request.getNote());

        Point savedPoint = pointRepository.save(point);

        log.info("상벌점 부여 완료: pointId={}, residentName={}, type={}, points={}",
                savedPoint.getPointId(), resident.getName(), pointType, request.getPoints());

        return new PointDetailResponse(new PointDto(savedPoint), "상벌점 부여 성공");
    }

    // 상벌점 목록 조회
    @Transactional(readOnly = true)
    public PointListResponse getPoints(int page, int limit, String residentId,
                                       String pointTypeStr, String category, String search) {
        log.info("상벌점 목록 조회: page={}, limit={}, residentId={}, type={}, category={}, search={}",
                page, limit, residentId, pointTypeStr, category, search);

        if (page < 0) page = 0;
        if (limit <= 0) limit = 10;
        if (limit > 100) limit = 100;

        // 상벌점 유형 변환
        PointType pointType = null;
        if (pointTypeStr != null && !pointTypeStr.trim().isEmpty()) {
            pointType = parsePointType(pointTypeStr);
        }

        // 검색어/카테고리 처리
        String searchTerm = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String categoryTerm = (category != null && !category.trim().isEmpty()) ? category.trim() : null;
        String residentIdTerm = (residentId != null && !residentId.trim().isEmpty()) ? residentId.trim() : null;

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Point> pointsPage = pointRepository.findPointsWithFilter(
                residentIdTerm, pointType, categoryTerm, searchTerm, pageable);

        List<PointDto> pointDtos = pointsPage.getContent().stream()
                .map(PointDto::new)
                .collect(Collectors.toList());

        log.info("상벌점 목록 조회 완료: 총 {}건", pointsPage.getTotalElements());

        return new PointListResponse(pointDtos, pointsPage.getTotalElements(), "조회 성공");
    }

    // 상벌점 상세 조회
    @Transactional(readOnly = true)
    public PointDetailResponse getPointDetail(String pointId) {
        log.info("상벌점 상세 조회: pointId={}", pointId);

        Point point = pointRepository.findByPointId(pointId)
                .orElseThrow(() -> new EntityNotFoundException("상벌점 기록을 찾을 수 없습니다: " + pointId));

        return new PointDetailResponse(new PointDto(point), "조회 성공");
    }

    // 상벌점 수정
    public PointDetailResponse updatePoint(String pointId, PointUpdateRequest request) {
        log.info("상벌점 수정 요청: pointId={}", pointId);

        Point point = pointRepository.findByPointId(pointId)
                .orElseThrow(() -> new EntityNotFoundException("상벌점 기록을 찾을 수 없습니다: " + pointId));

        PointType pointType = parsePointType(request.getPointType());
        validateDate(request.getIssuedDate());

        point.setPointType(pointType);
        point.setPoints(request.getPoints());
        point.setReason(request.getReason());
        point.setCategory(request.getCategory());
        point.setIssuedDate(request.getIssuedDate());
        point.setIssuedBy(request.getIssuedBy());
        point.setNote(request.getNote());

        Point updatedPoint = pointRepository.save(point);

        log.info("상벌점 수정 완료: pointId={}", pointId);

        return new PointDetailResponse(new PointDto(updatedPoint), "수정 성공");
    }

    // 상벌점 삭제
    public void deletePoint(String pointId) {
        log.info("상벌점 삭제 요청: pointId={}", pointId);

        Point point = pointRepository.findByPointId(pointId)
                .orElseThrow(() -> new EntityNotFoundException("상벌점 기록을 찾을 수 없습니다: " + pointId));

        pointRepository.delete(point);

        log.info("상벌점 삭제 완료: pointId={}", pointId);
    }

    // 입주자별 상벌점 합계 조회
    @Transactional(readOnly = true)
    public PointSummaryResponse getPointSummary(String residentId) {
        log.info("입주자 상벌점 합계 조회: residentId={}", residentId);

        Resident resident = residentRepository.findByResidentId(residentId)
                .orElseThrow(() -> new EntityNotFoundException("입주자를 찾을 수 없습니다: " + residentId));

        int totalMerit = pointRepository.sumMeritPointsByResidentId(residentId);
        int totalDemerit = pointRepository.sumDemeritPointsByResidentId(residentId);
        int netPoints = totalMerit - totalDemerit;

        log.info("상벌점 합계 조회 완료: residentId={}, 상점={}, 벌점={}, 순점수={}",
                residentId, totalMerit, totalDemerit, netPoints);

        return new PointSummaryResponse(
                residentId,
                resident.getName(),
                resident.getRoomNumber(),
                totalMerit,
                totalDemerit,
                netPoints,
                "조회 성공"
        );
    }

    // 입주자별 상벌점 목록 조회
    @Transactional(readOnly = true)
    public PointListResponse getPointsByResident(String residentId) {
        log.info("입주자별 상벌점 목록 조회: residentId={}", residentId);

        // 입주자 존재 확인
        if (!residentRepository.existsByResidentId(residentId)) {
            throw new EntityNotFoundException("입주자를 찾을 수 없습니다: " + residentId);
        }

        List<Point> points = pointRepository.findByResidentId(residentId);
        List<PointDto> pointDtos = points.stream()
                .map(PointDto::new)
                .collect(Collectors.toList());

        return new PointListResponse(pointDtos, pointDtos.size(), "조회 성공");
    }

    private PointType parsePointType(String pointTypeStr) {
        try {
            return PointType.valueOf(pointTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바르지 않은 상벌점 유형입니다: " + pointTypeStr +
                    " (MERIT 또는 DEMERIT만 가능)");
        }
    }

    private void validateDate(String date) {
        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다: " + date);
        }
    }
}
