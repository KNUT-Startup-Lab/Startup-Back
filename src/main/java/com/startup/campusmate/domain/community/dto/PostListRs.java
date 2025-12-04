package com.startup.campusmate.domain.community.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostListRs {
    private List<PostDto> data;
    private PaginationInfo pagination;

    @Getter
    @Builder
    public static class PaginationInfo {
        private int page;
        private int limit;
        private long total;
        private int totalPages;
    }
}
