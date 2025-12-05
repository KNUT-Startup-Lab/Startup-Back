package com.startup.campusmate.domain.qna.dto;

import com.startup.campusmate.domain.qna.entity.Qna;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class QnaDto {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String category;
    private String status;
    private List<String> images;
    private String answer;
    private Long adminId;
    private String adminName;
    private String createdAt;
    private String updatedAt;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static QnaDto from(Qna qna) {
        return QnaDto.builder()
                .id(qna.getId())
                .userId(qna.getUser().getId())
                .title(qna.getTitle())
                .content(qna.getContent())
                .category(qna.getCategory().getDisplayName())
                .status(qna.getStatus().getValue())
                .images(qna.getImages().stream()
                        .map(img -> img.getImageUrl())
                        .collect(Collectors.toList()))
                .answer(qna.getAnswer())
                .adminId(qna.getAdmin() != null ? qna.getAdmin().getId() : null)
                .adminName(qna.getAdmin() != null ? qna.getAdmin().getNickname() : null)
                .createdAt(qna.getCreateDate().format(FORMATTER))
                .updatedAt(qna.getModifyDate() != null ? qna.getModifyDate().format(FORMATTER) : null)
                .build();
    }
}
