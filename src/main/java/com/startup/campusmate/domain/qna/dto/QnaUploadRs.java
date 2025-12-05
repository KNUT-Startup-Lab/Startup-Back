package com.startup.campusmate.domain.qna.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QnaUploadRs {
    private String url;

    public static QnaUploadRs of(String url) {
        return QnaUploadRs.builder()
                .url(url)
                .build();
    }
}
