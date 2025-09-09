package com.startup.campusmate.domain.member.member.service;

import com.startup.campusmate.domain.member.member.dto.MemberDto;
import com.startup.campusmate.domain.member.member.dto.SignupRq;
import com.startup.campusmate.domain.member.member.entity.Member;
import com.startup.campusmate.domain.member.member.repository.MemberRepository;
import com.startup.campusmate.domain.member.social.repository.MemberSocialRepository;
import com.startup.campusmate.global.exceptions.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final FileUploadService fileUploadService;

    public void signup(SignupRq signupRq) {
        // 이메일 중복 체크
        if (memberRepository.existsByUsername(signupRq.getUsername())) {
            throw new GlobalException("이미 존재하는 유저이름입니다.");
        }

        // 학번 중복 체크
        if (memberRepository.existsByStudentNum(signupRq.getStudentNum())) {
            throw new GlobalException("이미 존재하는 학번입니다.");
        }

        // 사용자 저장
        Member member = Member.builder()
                .username(signupRq.getUsername())
                .password(passwordEncoder.encode(signupRq.getPassword())) // 실제로는 암호화해야 함
                .nickname(signupRq.getNickname())
                .studentNum(signupRq.getStudentNum())
                .college(signupRq.getCollege())
                .build();

        member.setAdmin(false);
        memberRepository.save(member);
    }

    @Transactional
    public void changePassword(String currentPassword, String newPassword) {
        // 1) 현재 인증된 유저 조회
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new GlobalException("사용자를 찾을 수 없습니다."));

        // 2) 현재 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new GlobalException("현재 비밀번호가 일치하지 않습니다.");
        }

        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    public boolean isEmailAvailable(String email) {
        return memberRepository.findByUsername(email).isEmpty();
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    @Transactional
    public Member modify(Long id, MemberDto dto) {
        // 1. 수정할 Member 엔티티를 DB에서 조회
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ID %d에 해당하는 회원이 없습니다.".formatted(id)));

        // 3. DTO로부터 받은 정보로 엔티티 필드 업데이트
        // ✨ 주의: username, password 등 민감하거나 변경되면 안 되는 정보는 여기서 수정하지 않도록 합니다.
        member.setNickname(dto.getNickname());
        member.setCollege(dto.getCollege());
        // ... 필요한 다른 필드들 업데이트

        // 4. @Transactional에 의해 메서드 종료 시 자동으로 DB에 저장(dirty checking)
        return member;
    }

    @Transactional
    public String updateProfileImage(Long id, MultipartFile profileImage) {
        // 1. 회원 조회
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ID %d에 해당하는 회원이 없습니다.".formatted(id)));

        // 2. 기존 이미지가 있다면 파일 시스템에서 삭제 (선택적)
        if (member.getProfileImageUrl() != null) {
            fileUploadService.deleteFile(member.getProfileImageUrl());
        }

        // 3. 새 이미지 파일을 서버에 저장하고, 접근 가능한 URL을 반환받음
        String imageUrl = fileUploadService.saveFile(profileImage);

        // 4. 회원 엔티티에 새 이미지 URL 저장
        member.setProfileImageUrl(imageUrl);

        return imageUrl;
    }

    @Transactional
    public void deleteProfileImage(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ID %d에 해당하는 회원이 없습니다.".formatted(id)));

        // 1. 파일 시스템에서 실제 이미지 파일 삭제
        if (member.getProfileImageUrl() != null) {
            fileUploadService.deleteFile(member.getProfileImageUrl());
        }

        // 2. DB에서 이미지 URL 정보 제거 (null로 업데이트)
        member.setProfileImageUrl(null);
    }

    @Transactional
    public void deleteAccount(Long id, String password) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ID %d에 해당하는 회원이 없습니다.".formatted(id)));

        // 1. 입력된 비밀번호와 DB의 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 2. 비밀번호가 일치하면 회원 정보 삭제
        memberRepository.delete(member);
    }
}