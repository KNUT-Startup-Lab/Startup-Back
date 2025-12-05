package com.startup.campusmate.domain.member.member.repository;

import com.startup.campusmate.domain.member.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 층별 회원 조회 (알림용)
    List<Member> findByFloor(Integer floor);
    // 학번으로 회원 조회 (알림용)
    Optional<Member> findByStudentNum(String studentNum);
    boolean existsByUsername(@NotBlank String username);
    boolean existsByStudentNum(String studentNum);
    Optional<Member> findByUsername(String email);
    Optional<Member> findByNicknameAndPhoneNum(String nickname, String phoneNum);
    Member findById(Member member);
    void deleteByRefreshToken(String refreshToken);

}
