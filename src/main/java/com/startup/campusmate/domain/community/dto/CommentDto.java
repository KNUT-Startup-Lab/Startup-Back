package com.startup.campusmate.domain.community.dto;

import com.startup.campusmate.domain.community.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class CommentDto {
    private Long id;
    private Long userId;
    private String content;
    private String createdAt;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt().format(FORMATTER))
                .build();
    }
}
