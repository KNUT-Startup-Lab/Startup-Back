package com.startup.campusmate.domain.push.repository;

import com.startup.campusmate.domain.push.entity.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PushTokenRepository extends JpaRepository<PushToken, Long> {

    Optional<PushToken> findByUserId(Long userId);

    Optional<PushToken> findByExpoPushToken(String expoPushToken);

    // 여러 사용자의 토큰 조회 (일괄 발송용)
    List<PushToken> findByUserIdIn(List<Long> userIds);

    // 토큰 삭제 (로그아웃 시)
    void deleteByUserId(Long userId);

    void deleteByExpoPushToken(String expoPushToken);
}