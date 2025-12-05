package com.startup.campusmate.domain.qna.entity;

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
public class Qna extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QnaCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QnaStatus status;

    @Lob
    private String answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Member admin;

    @OneToMany(mappedBy = "qna", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QnaImage> images = new ArrayList<>();

    @Builder
    public Qna(Member user, String title, String content, QnaCategory category) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.category = category;
        this.status = QnaStatus.PENDING;
    }

    public void addAnswer(String answer, Member admin) {
        this.answer = answer;
        this.admin = admin;
        this.status = QnaStatus.ANSWERED;
        this.setModified();
    }

    public void addImage(QnaImage image) {
        this.images.add(image);
        image.setQna(this);
    }

    public boolean isPending() {
        return this.status == QnaStatus.PENDING;
    }

    public boolean isOwnedBy(Long userId) {
        return this.user.getId().equals(userId);
    }
}
