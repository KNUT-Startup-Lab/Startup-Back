package com.startup.campusmate.domain.resident.resident.service;

import com.startup.campusmate.domain.resident.resident.dto.*;
import com.startup.campusmate.domain.resident.resident.entity.Resident;
import com.startup.campusmate.domain.resident.resident.repository.ResidentRepository;
import com.startup.campusmate.global.exceptions.DuplicateRoomException;
import com.startup.campusmate.global.exceptions.DuplicateStudentException;
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
public class ResidentService {

    private final ResidentRepository residentRepository;

    public ResidentService(ResidentRepository residentRepository) {
        this.residentRepository = residentRepository;
    }

    public ResidentRegistrationResponse registerResident(ResidentRegistrationRequest request) {
        log.info("입주자 등록 요청: 학번={}, 방번호={}", request.getStudentId(), request.getRoomNumber());

        // 방 번호 중복 체크
        if (residentRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new DuplicateRoomException("이미 사용 중인 방 번호입니다: " + request.getRoomNumber());
        }

        // 학번 중복 체크 (선택사항)
        if (residentRepository.existsByStudentId(request.getStudentId())) {
            throw new DuplicateStudentException("이미 등록된 학번입니다: " + request.getStudentId());
        }

        // 날짜 유효성 검증
        validateCheckInDate(request.getCheckInDate());

        // 입주자 정보 저장
        Resident resident = new Resident();
        resident.setStudentId(request.getStudentId());
        resident.setName(request.getName());
        resident.setRoomNumber(request.getRoomNumber());
        resident.setCheckInDate(request.getCheckInDate());
        resident.setPhone(request.getPhone());
        resident.setEmail(request.getEmail());
        resident.setStatus(ResidentStatus.ACTIVE);

        Resident savedResident = residentRepository.save(resident);

        log.info("입주자 등록 완료: ID={}, 방번호={}", savedResident.getResidentId(), savedResident.getRoomNumber());

        return new ResidentRegistrationResponse(savedResident.getResidentId(), "등록 성공");
    }

    @Transactional(readOnly = true)
    public ResidentListResponse getResidents(int page, int limit, String search, String statusStr) {
        log.info("입주자 목록 조회: page={}, limit={}, search={}, status={}", page, limit, search, statusStr);

        // 페이지 유효성 검사
        if (page < 0) page = 0;
        if (limit <= 0) limit = 10;
        if (limit > 100) limit = 100; // 최대 제한

        // 상태 변환
        ResidentStatus status = null;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            try {
                status = ResidentStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("올바르지 않은 상태값입니다: " + statusStr);
            }
        }

        // 검색어 처리
        String searchTerm = null;
        if (search != null && !search.trim().isEmpty()) {
            searchTerm = search.trim();
        }

        // Pageable 객체 생성 (정렬: 생성일시 내림차순)
        Pageable pageable = PageRequest.of(page, limit,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        // 데이터 조회
        Page<Resident> residentsPage = residentRepository.findResidentsWithFilter(
                searchTerm, status, pageable);

        // DTO 변환
        List<ResidentDto> residentDtos = residentsPage.getContent().stream()
                .map(ResidentDto::new)
                .collect(Collectors.toList());

        long totalCount = residentsPage.getTotalElements();

        log.info("입주자 목록 조회 완료: 총 {}건, 현재 페이지 {}건", totalCount, residentDtos.size());

        return new ResidentListResponse(residentDtos, totalCount, "조회 성공");
    }

    @Transactional(readOnly = true)
    public ResidentDetailResponse getResidentDetail(String residentId) {
        log.info("입주자 상세 조회: residentId={}", residentId);

        Resident resident = residentRepository.findByResidentId(residentId)
                .orElseThrow(() -> new EntityNotFoundException("입주자를 찾을 수 없습니다: " + residentId));

        ResidentDto residentDto = new ResidentDto(resident);

        log.info("입주자 상세 조회 완료: residentId={}, name={}", residentId, resident.getName());

        return new ResidentDetailResponse(residentDto, "조회 성공");
    }

    @Transactional
    public ResidentDetailResponse updateResident(String residentId, ResidentUpdateRequest request) {
        log.info("입주자 정보 수정: residentId={}, name={}, roomNumber={}",
                residentId, request.getName(), request.getRoomNumber());

        Resident resident = residentRepository.findByResidentId(residentId)
                .orElseThrow(() -> new EntityNotFoundException("입주자를 찾을 수 없습니다: " + residentId));

        // 방 번호 변경 시 중복 체크 (본인 제외)
        if (!resident.getRoomNumber().equals(request.getRoomNumber())) {
            if (residentRepository.existsByRoomNumberAndResidentIdNot(request.getRoomNumber(), residentId)) {
                throw new DuplicateRoomException("이미 사용 중인 방 번호입니다: " + request.getRoomNumber());
            }
        }

        // 정보 업데이트
        resident.setName(request.getName());
        resident.setRoomNumber(request.getRoomNumber());
        resident.setPhone(request.getPhone());
        resident.setEmail(request.getEmail());

        Resident updatedResident = residentRepository.save(resident);
        ResidentDto residentDto = new ResidentDto(updatedResident);

        log.info("입주자 정보 수정 완료: residentId={}", residentId);

        return new ResidentDetailResponse(residentDto, "수정 성공");
    }

    @Transactional
    public void deleteResident(String residentId) {
        log.info("입주자 삭제: residentId={}", residentId);

        Resident resident = residentRepository.findByResidentId(residentId)
                .orElseThrow(() -> new EntityNotFoundException("입주자를 찾을 수 없습니다: " + residentId));

        residentRepository.delete(resident);

        log.info("입주자 삭제 완료: residentId={}, name={}", residentId, resident.getName());
    }

    @Transactional
    public ResidentDetailResponse changeResidentStatus(String residentId, ResidentStatusChangeRequest request) {
        log.info("입주자 상태 변경: residentId={}, status={}, checkOutDate={}",
                residentId, request.getStatus(), request.getCheckOutDate());

        Resident resident = residentRepository.findByResidentId(residentId)
                .orElseThrow(() -> new EntityNotFoundException("입주자를 찾을 수 없습니다: " + residentId));

        // 상태 변환
        ResidentStatus newStatus;
        try {
            newStatus = ResidentStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바르지 않은 상태값입니다: " + request.getStatus());
        }

        // 퇴실 처리 시 퇴실일 검증
        if (newStatus == ResidentStatus.INACTIVE) {
            if (request.getCheckOutDate() != null) {
                validateCheckOutDate(request.getCheckOutDate());
                resident.setCheckOutDate(request.getCheckOutDate());
            }
        } else if (newStatus == ResidentStatus.ACTIVE) {
            // 입주 상태로 변경 시 퇴실일 초기화
            resident.setCheckOutDate(null);
        }

        resident.setStatus(newStatus);

        Resident updatedResident = residentRepository.save(resident);
        ResidentDto residentDto = new ResidentDto(updatedResident);

        log.info("입주자 상태 변경 완료: residentId={}, status={}", residentId, newStatus);

        return new ResidentDetailResponse(residentDto, "상태 변경 성공");
    }

    private void validateCheckInDate(String checkInDate) {
        try {
            LocalDate date = LocalDate.parse(checkInDate);
            LocalDate today = LocalDate.now();

            // 입주일이 과거가 아닌지 체크 (선택사항)
            if (date.isBefore(today)) {
                log.warn("과거 날짜로 입주 등록: {}", checkInDate);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다: " + checkInDate);
        }
    }

    private void validateCheckOutDate(String checkOutDate) {
        try {
            LocalDate.parse(checkOutDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다: " + checkOutDate);
        }
    }

    @Transactional
    public void updateResidentStatus(String residentId, ResidentStatus newStatus, String checkOutDate) {
        Resident resident = residentRepository.findByResidentId(residentId)
                .orElseThrow(() -> new EntityNotFoundException("입주자를 찾을 수 없습니다: " + residentId));

        resident.setStatus(newStatus);
        if (newStatus == ResidentStatus.INACTIVE && checkOutDate != null) {
            resident.setCheckOutDate(checkOutDate);
        } else if (newStatus == ResidentStatus.ACTIVE) {
            resident.setCheckOutDate(null);
        }

        residentRepository.save(resident);
        log.info("입주자 상태 업데이트: ID={}, 상태={}", residentId, newStatus);
    }

    // 방 번호 중복 체크 전용 메서드
    public boolean isRoomNumberAvailable(String roomNumber) {
        return !residentRepository.existsByRoomNumber(roomNumber);
    }
}