package com.startup.campusmate.domain.qna.entity;

import com.startup.campusmate.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_id", nullable = false)
    private Qna qna;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public QnaImage(String imageUrl) {
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
    }
}
