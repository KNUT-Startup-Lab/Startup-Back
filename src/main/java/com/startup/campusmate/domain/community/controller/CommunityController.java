package com.startup.campusmate.domain.community.controller;

import com.startup.campusmate.domain.community.dto.*;
import com.startup.campusmate.domain.community.service.CommunityService;
import com.startup.campusmate.domain.member.member.entity.Member;
import com.startup.campusmate.domain.member.member.repository.MemberRepository;
import com.startup.campusmate.global.exceptions.GlobalException;
import com.startup.campusmate.global.rsData.RsData;
import com.startup.campusmate.global.security.SecurityUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final MemberRepository memberRepository;

    @GetMapping("/posts")
    public RsData<PostListRs> getPostList(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int limit
    ) {
        Integer userFloor = getUserFloor(user.getId(), floor);
        PostListRs result = communityService.getPostList(user.getId(), userFloor, page, limit);
        return RsData.of("200-1", "게시글 목록 조회 성공", result);
    }

    @GetMapping("/posts/{id}")
    public RsData<PostDetailRs> getPostDetail(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long id
    ) {
        PostDetailRs result = communityService.getPostDetail(id, user.getId());
        return RsData.of("200-1", "게시글 상세 조회 성공", result);
    }

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<Long> createPost(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody PostCreateRq request
    ) {
        Long postId = communityService.createPost(user.getId(), request);
        return RsData.of("201-1", "게시글이 등록되었습니다.", postId);
    }

    @PutMapping("/posts/{id}")
    public RsData<String> updatePost(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateRq request
    ) {
        communityService.updatePost(id, user.getId(), request);
        return RsData.of("200-1", "게시글이 수정되었습니다.");
    }

    @DeleteMapping("/posts/{id}")
    public RsData<String> deletePost(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long id
    ) {
        communityService.deletePost(id, user.getId());
        return RsData.of("200-1", "게시글이 삭제되었습니다.");
    }

    @PostMapping("/posts/{id}/like")
    public RsData<LikeRs> toggleLike(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long id
    ) {
        LikeRs result = communityService.toggleLike(id, user.getId());
        return RsData.of("200-1", "좋아요 처리 완료", result);
    }

    @PostMapping("/posts/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<Long> createComment(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long id,
            @Valid @RequestBody CommentCreateRq request
    ) {
        Long commentId = communityService.createComment(id, user.getId(), request);
        return RsData.of("201-1", "댓글이 등록되었습니다.", commentId);
    }

    @DeleteMapping("/comments/{id}")
    public RsData<String> deleteComment(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long id
    ) {
        communityService.deleteComment(id, user.getId());
        return RsData.of("200-1", "댓글이 삭제되었습니다.");
    }

    @GetMapping("/notices")
    public RsData<NoticeListRs> getNoticeList(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false, defaultValue = "5") int limit
    ) {
        Integer userFloor = getUserFloor(user.getId(), floor);
        NoticeListRs result = communityService.getNoticeList(userFloor, limit);
        return RsData.of("200-1", "공지사항 목록 조회 성공", result);
    }

    @GetMapping("/notices/{id}")
    public RsData<NoticeDto> getNoticeDetail(@PathVariable Long id) {
        NoticeDto result = communityService.getNoticeDetail(id);
        return RsData.of("200-1", "공지사항 상세 조회 성공", result);
    }

    @PostMapping("/notices")
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<Long> createNotice(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody NoticeCreateRq request
    ) {
        Long noticeId = communityService.createNotice(user.getId(), request);
        return RsData.of("201-1", "공지가 등록되었습니다.", noticeId);
    }

    private Integer getUserFloor(Long userId, Integer requestedFloor) {
        if (requestedFloor != null) {
            return requestedFloor;
        }
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new GlobalException("404", "사용자를 찾을 수 없습니다."));
        return member.getFloor();
    }
}
