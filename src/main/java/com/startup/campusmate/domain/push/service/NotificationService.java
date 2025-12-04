package com.startup.campusmate.domain.push.service;

import com.startup.campusmate.domain.push.repository.PushTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private ExpoPushService expoPushService;

    @Autowired
    private PushTokenRepository pushTokenRepository;

    // 특정 사용자에게 알림 보내기
    public void notifyUser(Long userId, String title, String message) {
        pushTokenRepository.findByUserId(userId)
                .ifPresent(token -> {
                    expoPushService.sendPushNotification(
                            token.getExpoPushToken(),
                            title,
                            message
                    );
                });
    }

    // 데이터와 함께 알림 (화면 이동 등)
    public void notifyWithData(Long userId, String title, String message, String screen) {
        pushTokenRepository.findByUserId(userId)
                .ifPresent(token -> {
                    Map<String, Object> data = Map.of("screen", screen);
                    expoPushService.sendPushNotification(
                            token.getExpoPushToken(),
                            title,
                            message,
                            data
                    );
                });
    }
}