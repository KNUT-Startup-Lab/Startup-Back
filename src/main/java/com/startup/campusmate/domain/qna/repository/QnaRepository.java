package com.startup.campusmate.domain.qna.repository;

import com.startup.campusmate.domain.qna.entity.Qna;
import com.startup.campusmate.domain.qna.entity.QnaCategory;
import com.startup.campusmate.domain.qna.entity.QnaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    @Query("SELECT q FROM Qna q WHERE " +
            "(:status IS NULL OR q.status = :status) AND " +
            "(:category IS NULL OR q.category = :category) " +
            "ORDER BY q.createDate DESC")
    Page<Qna> findAllWithFilter(
            @Param("status") QnaStatus status,
            @Param("category") QnaCategory category,
            Pageable pageable
    );

    @Query("SELECT q FROM Qna q WHERE q.user.id = :userId AND " +
            "(:status IS NULL OR q.status = :status) " +
            "ORDER BY q.createDate DESC")
    Page<Qna> findByUserIdWithFilter(
            @Param("userId") Long userId,
            @Param("status") QnaStatus status,
            Pageable pageable
    );

    long countByStatus(QnaStatus status);

    long countByUserIdAndStatus(Long userId, QnaStatus status);

    long countByUserId(Long userId);
}
