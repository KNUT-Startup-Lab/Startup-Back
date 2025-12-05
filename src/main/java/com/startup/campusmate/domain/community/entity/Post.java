package com.startup.campusmate.domain.community.entity;

import com.startup.campusmate.domain.member.member.entity.Member;
import com.startup.campusmate.global.jpa.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_post_floor_created", columnList = "floor, createDate DESC")
})
public class Post extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member user;

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private Integer likes = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(Member user, Integer floor, String content) {
        this.user = user;
        this.floor = floor;
        this.content = content;
        this.likes = 0;
    }

    public void updateContent(String content) {
        this.content = content;
        this.setModified();
    }

    public void incrementLikes() {
        this.likes++;
    }

    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
    }

    public boolean isOwnedBy(Long userId) {
        return this.user.getId().equals(userId);
    }

    public int getCommentCount() {
        return this.comments.size();
    }
}
