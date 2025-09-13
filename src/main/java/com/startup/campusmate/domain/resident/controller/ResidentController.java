package com.startup.campusmate.domain.resident.controller;

import com.startup.campusmate.domain.resident.dto.*;
import com.startup.campusmate.domain.resident.service.ResidentService;
import com.startup.campusmate.global.exceptions.DuplicateRoomException;
import com.startup.campusmate.global.exceptions.DuplicateStudentException;
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
public class ResidentController {

    private final ResidentService residentService;

    public ResidentController(ResidentService residentService) {
        this.residentService = residentService;
    }

    @PostMapping("/residents")
    public ResponseEntity<?> registerResident(
            @Valid @RequestBody ResidentRegistrationRequest request) {

        try {
            ResidentRegistrationResponse response = residentService.registerResident(request);
            return ResponseEntity.ok(response);

        } catch (DuplicateRoomException e) {
            log.warn("방 번호 중복: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "DUPLICATE_ROOM", "message", e.getMessage()));

        } catch (DuplicateStudentException e) {
            log.warn("학번 중복: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "DUPLICATE_STUDENT", "message", e.getMessage()));

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "INVALID_INPUT", "message", e.getMessage()));

        } catch (Exception e) {
            log.error("입주자 등록 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다"));
        }
    }

    @GetMapping("/residents")
    public ResponseEntity<?> getResidents(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status) {

        try {
            log.info("입주자 목록 조회 요청: page={}, limit={}, search={}, status={}",
                    page, limit, search, status);

            ResidentListResponse response = residentService.getResidents(page, limit, search, status);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "INVALID_PARAMETER", "message", e.getMessage()));

        } catch (Exception e) {
            log.error("입주자 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다"));
        }
    }

    @GetMapping("/residents/{id}")
    public ResponseEntity<?> getResidentDetail(@PathVariable String id) {
        try {
            log.info("입주자 상세 조회 요청: residentId={}", id);

            ResidentDetailResponse response = residentService.getResidentDetail(id);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            log.warn("입주자 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("입주자 상세 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다"));
        }
    }

    @PutMapping("/residents/{id}")
    public ResponseEntity<?> updateResident(
            @PathVariable String id,
            @Valid @RequestBody ResidentUpdateRequest request) {

        try {
            log.info("입주자 정보 수정 요청: residentId={}", id);

            ResidentDetailResponse response = residentService.updateResident(id, request);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            log.warn("입주자 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (DuplicateRoomException e) {
            log.warn("방 번호 중복: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "DUPLICATE_ROOM", "message", e.getMessage()));

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "INVALID_INPUT", "message", e.getMessage()));

        } catch (Exception e) {
            log.error("입주자 정보 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다"));
        }
    }

    @DeleteMapping("/residents/{id}")
    public ResponseEntity<?> deleteResident(@PathVariable String id) {
        try {
            log.info("입주자 삭제 요청: residentId={}", id);

            residentService.deleteResident(id);
            return ResponseEntity.ok(Map.of("message", "삭제 성공"));

        } catch (EntityNotFoundException e) {
            log.warn("입주자 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("입주자 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다"));
        }
    }

    @PutMapping("/residents/{id}/status")
    public ResponseEntity<?> changeResidentStatus(
            @PathVariable String id,
            @Valid @RequestBody ResidentStatusChangeRequest request) {

        try {
            log.info("입주자 상태 변경 요청: residentId={}, status={}", id, request.getStatus());

            ResidentDetailResponse response = residentService.changeResidentStatus(id, request);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            log.warn("입주자 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "INVALID_INPUT", "message", e.getMessage()));

        } catch (Exception e) {
            log.error("입주자 상태 변경 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다"));
        }
    }

    // 방 번호 중복 체크 전용 API (선택사항)
    @GetMapping("/residents/check-room/{roomNumber}")
    public ResponseEntity<?> checkRoomAvailability(@PathVariable String roomNumber) {
        boolean available = residentService.isRoomNumberAvailable(roomNumber);
        return ResponseEntity.ok(Map.of(
                "roomNumber", roomNumber,
                "available", available,
                "message", available ? "사용 가능한 방 번호입니다" : "이미 사용 중인 방 번호입니다"
        ));
    }

    // 입주자 상태 업데이트 API (퇴실 처리 등) - 기존 메서드 유지
    @PatchMapping("/residents/{residentId}/status")
    public ResponseEntity<?> updateResidentStatus(
            @PathVariable String residentId,
            @RequestBody Map<String, String> requestBody) {

        try {
            String statusStr = requestBody.get("status");
            String checkOutDate = requestBody.get("checkOutDate");

            if (statusStr == null || statusStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "INVALID_INPUT", "message", "상태값이 필요합니다"));
            }

            ResidentStatus status = ResidentStatus.valueOf(statusStr.toUpperCase());
            residentService.updateResidentStatus(residentId, status, checkOutDate);

            return ResponseEntity.ok(Map.of("message", "상태 업데이트 성공"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "INVALID_STATUS", "message", "올바르지 않은 상태값입니다"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("상태 업데이트 중 오류 발생", e);
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
