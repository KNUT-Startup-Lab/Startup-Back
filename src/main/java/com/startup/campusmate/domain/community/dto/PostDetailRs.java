package com.startup.campusmate.domain.community.dto;

import com.startup.campusmate.domain.community.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
public class PostDetailRs {
    private Long id;
    private Long userId;
    private Integer floor;
    private String author;
    private String content;
    private Integer likes;
    private Boolean likedByMe;
    private String createdAt;
    private List<CommentDto> comments;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static PostDetailRs from(Post post, boolean likedByMe, List<CommentDto> comments) {
        return PostDetailRs.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .floor(post.getFloor())
                .author("익명")
                .content(post.getContent())
                .likes(post.getLikes())
                .likedByMe(likedByMe)
                .createdAt(post.getCreateDate().format(FORMATTER))
                .comments(comments)
                .build();
    }
}
