package com.startup.campusmate.domain.community.repository;

import com.startup.campusmate.domain.community.entity.CommunityNotice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityNoticeRepository extends JpaRepository<CommunityNotice, Long> {

    @Query("SELECT n FROM CommunityNotice n WHERE n.floor = :floor OR n.floor = 0 ORDER BY n.createdAt DESC")
    List<CommunityNotice> findByFloorOrGlobal(@Param("floor") Integer floor, Pageable pageable);
}
