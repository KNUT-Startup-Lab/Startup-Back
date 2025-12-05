package com.startup.campusmate.domain.community.dto;

import com.startup.campusmate.domain.community.entity.CommunityNotice;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class NoticeDto {
    private Long id;
    private Integer floor;
    private String title;
    private String content;
    private String createdAt;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static NoticeDto from(CommunityNotice notice) {
        return NoticeDto.builder()
                .id(notice.getId())
                .floor(notice.getFloor())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt().format(FORMATTER))
                .build();
    }
}
