package com.startup.campusmate.domain.community.entity;

import com.startup.campusmate.domain.member.member.entity.Member;
import com.startup.campusmate.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_post_like", columnNames = {"post_id", "user_id"})
})
public class PostLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public PostLike(Post post, Member user) {
        this.post = post;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
}
