package com.startup.campusmate.domain.community.dto;

import com.startup.campusmate.domain.community.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class PostDto {
    private Long id;
    private Long userId;
    private Integer floor;
    private String author;
    private String content;
    private Integer likes;
    private Boolean likedByMe;
    private Integer commentCount;
    private String createdAt;
    private String updatedAt;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static PostDto from(Post post, boolean likedByMe) {
        return PostDto.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .floor(post.getFloor())
                .author("익명")
                .content(post.getContent())
                .likes(post.getLikes())
                .likedByMe(likedByMe)
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreateDate().format(FORMATTER))
                .updatedAt(post.getModifyDate() != null ? post.getModifyDate().format(FORMATTER) : null)
                .build();
    }
}
