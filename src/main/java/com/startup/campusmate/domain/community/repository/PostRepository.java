package com.startup.campusmate.domain.community.repository;

import com.startup.campusmate.domain.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.floor = :floor ORDER BY p.createDate DESC")
    Page<Post> findByFloor(@Param("floor") Integer floor, Pageable pageable);
}
