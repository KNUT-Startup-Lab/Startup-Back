package com.startup.campusmate.domain.resident.controller;

import com.startup.campusmate.domain.resident.dto.ResidentRegistrationRequest;
import com.startup.campusmate.domain.resident.dto.ResidentRegistrationResponse;
import com.startup.campusmate.domain.resident.service.ResidentService;
import com.startup.campusmate.global.exceptions.DuplicateRoomException;
import com.startup.campusmate.global.exceptions.DuplicateStudentException;
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