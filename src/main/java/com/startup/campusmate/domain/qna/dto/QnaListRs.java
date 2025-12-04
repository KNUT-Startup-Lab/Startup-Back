package com.startup.campusmate.domain.qna.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QnaListRs {
    private List<QnaDto> data;
    private PaginationInfo pagination;
    private QnaStats stats;

    @Getter
    @Builder
    public static class PaginationInfo {
        private int page;
        private int limit;
        private long total;
        private int totalPages;
    }

    @Getter
    @Builder
    public static class QnaStats {
        private long total;
        private long pending;
        private long answered;
    }
}
