package com.startup.campusmate.domain.resident.service;

import com.startup.campusmate.domain.resident.dto.ResidentRegistrationRequest;
import com.startup.campusmate.domain.resident.dto.ResidentRegistrationResponse;
import com.startup.campusmate.domain.resident.entity.Resident;
import com.startup.campusmate.domain.resident.repository.ResidentRepository;
import com.startup.campusmate.global.exceptions.DuplicateRoomException;
import com.startup.campusmate.global.exceptions.DuplicateStudentException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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

        Resident savedResident = residentRepository.save(resident);

        log.info("입주자 등록 완료: ID={}, 방번호={}", savedResident.getResidentId(), savedResident.getRoomNumber());

        return new ResidentRegistrationResponse(savedResident.getResidentId(), "등록 성공");
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

    // 방 번호 중복 체크 전용 메서드
    public boolean isRoomNumberAvailable(String roomNumber) {
        return !residentRepository.existsByRoomNumber(roomNumber);
    }
}