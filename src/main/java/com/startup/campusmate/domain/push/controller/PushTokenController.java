package com.startup.campusmate.domain.push.controller;

import com.startup.campusmate.domain.push.dto.PushTokenRequest;
import com.startup.campusmate.domain.push.entity.PushToken;
import com.startup.campusmate.domain.push.repository.PushTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/push")
public class PushTokenController {

    @Autowired
    private PushTokenRepository pushTokenRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerToken(@RequestBody PushTokenRequest request) {
        // 기존 토큰이 있으면 업데이트, 없으면 생성
        PushToken token = pushTokenRepository.findByUserId(request.getUserId())
                .orElse(new PushToken());

        token.setUserId(request.getUserId());
        token.setExpoPushToken(request.getExpoPushToken());
        token.setCreatedAt(LocalDateTime.now());

        pushTokenRepository.save(token);

        return ResponseEntity.ok().build();
    }
}