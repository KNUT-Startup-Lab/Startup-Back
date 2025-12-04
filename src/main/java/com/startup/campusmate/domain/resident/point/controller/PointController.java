package com.startup.campusmate.domain.resident.point.controller;

import com.startup.campusmate.domain.point.dto.*;
import com.startup.campusmate.domain.resident.point.dto.*;
import com.startup.campusmate.domain.resident.resident.point.dto.*;
import com.startup.campusmate.domain.resident.point.service.PointService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Slf4j
@Validated
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    // 상벌점 부여
    @PostMapping("/points")
    public ResponseEntity<?> createPoint(@Valid @RequestBody PointCreateRequest request) {
        try {
            PointDetailResponse response = pointService.createPoint(request);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            log.warn("입주자 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "INVALID_INPUT", "message", e.getMessage()));

        } catch (Exception e) {
            log.error("상벌점 부여 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다"));
        }
    }

    // 상벌점 목록 조회
    @GetMapping("/points")
    public ResponseEntity<?> getPoints(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "residentId", required = false) String residentId,
            @RequestParam(value = "type", required = false) String pointType,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "search", required = false) String search) {

        try {
            log.info("상벌점 목록 조회 요청: page={}, limit={}, residentId={}, type={}, category={}, search={}",
                    page, limit, residentId, pointType, category, search);

            PointListResponse response = pointService.getPoints(
                    page, limit, residentId, pointType, category, search);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "INVALID_PARAMETER", "message", e.getMessage()));

        } catch (Exception e) {
            log.error("상벌점 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다"));
        }
    }

    // 상벌점 상세 조회
    @GetMapping("/points/{pointId}")
    public ResponseEntity<?> getPointDetail(@PathVariable String pointId) {
        try {
            log.info("상벌점 상세 조회 요청: pointId={}", pointId);

            PointDetailResponse response = pointService.getPointDetail(pointId);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            log.warn("상벌점 기록 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("상벌점 상세 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다"));
        }
    }

    // 상벌점 수정
    @PutMapping("/points/{pointId}")
    public ResponseEntity<?> updatePoint(
            @PathVariable String pointId,
            @Valid @RequestBody PointUpdateRequest request) {

        try {
            log.info("상벌점 수정 요청: pointId={}", pointId);

            PointDetailResponse response = pointService.updatePoint(pointId, request);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            log.warn("상벌점 기록 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "INVALID_INPUT", "message", e.getMessage()));

        } catch (Exception e) {
            log.error("상벌점 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다"));
        }
    }

    // 상벌점 삭제
    @DeleteMapping("/points/{pointId}")
    public ResponseEntity<?> deletePoint(@PathVariable String pointId) {
        try {
            log.info("상벌점 삭제 요청: pointId={}", pointId);

            pointService.deletePoint(pointId);
            return ResponseEntity.ok(Map.of("message", "삭제 성공"));

        } catch (EntityNotFoundException e) {
            log.warn("상벌점 기록 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("상벌점 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다"));
        }
    }

    // 입주자별 상벌점 합계 조회
    @GetMapping("/residents/{residentId}/points/summary")
    public ResponseEntity<?> getPointSummary(@PathVariable String residentId) {
        try {
            log.info("입주자 상벌점 합계 조회 요청: residentId={}", residentId);

            PointSummaryResponse response = pointService.getPointSummary(residentId);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            log.warn("입주자 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("상벌점 합계 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다"));
        }
    }

    // 입주자별 상벌점 목록 조회
    @GetMapping("/residents/{residentId}/points")
    public ResponseEntity<?> getPointsByResident(@PathVariable String residentId) {
        try {
            log.info("입주자별 상벌점 목록 조회 요청: residentId={}", residentId);

            PointListResponse response = pointService.getPointsByResident(residentId);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            log.warn("입주자 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("입주자별 상벌점 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다"));
        }
    }

    // Validation 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(Map.of(
                "error", "VALIDATION_ERROR",
                "message", "입력값이 올바르지 않습니다",
                "details", errors
        ));
    }
}