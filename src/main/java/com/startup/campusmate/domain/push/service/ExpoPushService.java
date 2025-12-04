package com.startup.campusmate.domain.push.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpoPushService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendPushNotification(String expoPushToken, String title, String body) {
        sendPushNotification(expoPushToken, title, body, null);
    }

    public void sendPushNotification(String expoPushToken, String title, String body, Map<String, Object> data) {
        Map<String, Object> message = new HashMap<>();
        message.put("to", expoPushToken);
        message.put("sound", "default");
        message.put("title", title);
        message.put("body", body);

        if (data != null) {
            message.put("data", data);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Push 전송 실패: " + response.getBody());
        }
    }

    // 여러 사용자에게 일괄 전송
    public void sendBulkPushNotification(List<String> tokens, String title, String body) {
        List<Map<String, Object>> messages = tokens.stream()
                .map(token -> {
                    Map<String, Object> msg = new HashMap<>();
                    msg.put("to", token);
                    msg.put("sound", "default");
                    msg.put("title", title);
                    msg.put("body", body);
                    return msg;
                })
                .collect(Collectors.toList());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(messages, headers);

        restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);
    }
}