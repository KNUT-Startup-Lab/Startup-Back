package com.startup.campusmate.domain.qna.repository;

import com.startup.campusmate.domain.qna.entity.QnaImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaImageRepository extends JpaRepository<QnaImage, Long> {
    List<QnaImage> findByQnaId(Long qnaId);
}
