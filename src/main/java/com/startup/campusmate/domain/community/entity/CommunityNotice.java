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
@Table(indexes = {
        @Index(name = "idx_notice_floor_created", columnList = "floor, createdAt DESC")
})
public class CommunityNotice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Member admin;

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public CommunityNotice(Member admin, Integer floor, String title, String content) {
        this.admin = admin;
        this.floor = floor;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
}
