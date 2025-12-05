package com.startup.campusmate.domain.push.service;

import com.startup.campusmate.domain.member.member.entity.Member;
import com.startup.campusmate.domain.member.member.repository.MemberRepository;
import com.startup.campusmate.domain.push.entity.PushToken;
import com.startup.campusmate.domain.push.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ExpoPushService expoPushService;
    private final PushTokenRepository pushTokenRepository;
    private final MemberRepository memberRepository;

    // 특정 사용자에게 알림 보내기
    @Async
    public void notifyUser(Long userId, String title, String message) {
        pushTokenRepository.findByUserId(userId)
                .ifPresent(token -> {
                    try {
                        expoPushService.sendPushNotification(
                                token.getExpoPushToken(),
                                title,
                                message
                        );
                        log.info("알림 전송 완료: userId={}, title={}", userId, title);
                    } catch (Exception e) {
                        log.error("알림 전송 실패: userId={}, error={}", userId, e.getMessage());
                    }
                });
    }

    // 데이터와 함께 알림 (화면 이동 등)
    @Async
    public void notifyWithData(Long userId, String title, String message, String screen) {
        pushTokenRepository.findByUserId(userId)
                .ifPresent(token -> {
                    try {
                        Map<String, Object> data = Map.of("screen", screen);
                        expoPushService.sendPushNotification(
                                token.getExpoPushToken(),
                                title,
                                message,
                                data
                        );
                        log.info("알림 전송 완료 (with data): userId={}, screen={}", userId, screen);
                    } catch (Exception e) {
                        log.error("알림 전송 실패: userId={}, error={}", userId, e.getMessage());
                    }
                });
    }

    // 전체 사용자에게 알림 보내기
    @Async
    public void notifyAll(String title, String message) {
        List<PushToken> allTokens = pushTokenRepository.findAll();
        if (allTokens.isEmpty()) {
            log.info("등록된 푸시 토큰이 없습니다.");
            return;
        }

        List<String> tokens = allTokens.stream()
                .map(PushToken::getExpoPushToken)
                .toList();

        try {
            expoPushService.sendBulkPushNotification(tokens, title, message);
            log.info("전체 알림 전송 완료: count={}, title={}", tokens.size(), title);
        } catch (Exception e) {
            log.error("전체 알림 전송 실패: error={}", e.getMessage());
        }
    }

    // 특정 층 사용자들에게 알림 보내기
    @Async
    public void notifyFloor(Integer floor, String title, String message) {
        List<Member> members = memberRepository.findByFloor(floor);
        if (members.isEmpty()) {
            log.info("해당 층에 사용자가 없습니다: floor={}", floor);
            return;
        }

        List<Long> userIds = members.stream()
                .map(Member::getId)
                .toList();

        List<PushToken> tokens = pushTokenRepository.findByUserIdIn(userIds);
        if (tokens.isEmpty()) {
            log.info("해당 층에 등록된 푸시 토큰이 없습니다: floor={}", floor);
            return;
        }

        List<String> tokenStrings = tokens.stream()
                .map(PushToken::getExpoPushToken)
                .toList();

        try {
            expoPushService.sendBulkPushNotification(tokenStrings, title, message);
            log.info("층별 알림 전송 완료: floor={}, count={}, title={}", floor, tokenStrings.size(), title);
        } catch (Exception e) {
            log.error("층별 알림 전송 실패: floor={}, error={}", floor, e.getMessage());
        }
    }

    // 전체 또는 특정 층에 알림 (floor=0이면 전체)
    @Async
    public void notifyFloorOrAll(Integer floor, String title, String message) {
        if (floor == null || floor == 0) {
            notifyAll(title, message);
        } else {
            notifyFloor(floor, title, message);
        }
    }
}