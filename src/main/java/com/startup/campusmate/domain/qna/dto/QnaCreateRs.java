package com.startup.campusmate.domain.qna.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QnaCreateRs {
    private Long id;
    private String message;

    public static QnaCreateRs of(Long id, String message) {
        return QnaCreateRs.builder()
                .id(id)
                .message(message)
                .build();
    }
}
