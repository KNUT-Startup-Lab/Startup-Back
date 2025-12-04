package com.startup.campusmate.domain.community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeRs {
    private Boolean liked;
    private Integer likes;

    public static LikeRs of(boolean liked, int likes) {
        return LikeRs.builder()
                .liked(liked)
                .likes(likes)
                .build();
    }
}
