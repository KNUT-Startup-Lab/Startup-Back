package com.startup.campusmate.domain.community.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NoticeListRs {
    private List<NoticeDto> data;

    public static NoticeListRs of(List<NoticeDto> data) {
        return NoticeListRs.builder()
                .data(data)
                .build();
    }
}
