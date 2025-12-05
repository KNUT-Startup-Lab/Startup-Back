package com.startup.campusmate.domain.community.service;

import com.startup.campusmate.domain.community.dto.*;
import com.startup.campusmate.domain.community.entity.Comment;
import com.startup.campusmate.domain.community.entity.CommunityNotice;
import com.startup.campusmate.domain.community.entity.Post;
import com.startup.campusmate.domain.community.entity.PostLike;
import com.startup.campusmate.domain.community.repository.CommentRepository;
import com.startup.campusmate.domain.community.repository.CommunityNoticeRepository;
import com.startup.campusmate.domain.community.repository.PostLikeRepository;
import com.startup.campusmate.domain.community.repository.PostRepository;
import com.startup.campusmate.domain.member.member.entity.Member;
import com.startup.campusmate.domain.member.member.repository.MemberRepository;
import com.startup.campusmate.domain.push.service.NotificationService;
import com.startup.campusmate.global.exceptions.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final CommunityNoticeRepository communityNoticeRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    public PostListRs getPostList(Long userId, Integer floor, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Post> postPage = postRepository.findByFloor(floor, pageable);

        List<PostDto> data = postPage.getContent().stream()
                .map(post -> {
                    boolean likedByMe = postLikeRepository.existsByPostIdAndUserId(post.getId(), userId);
                    return PostDto.from(post, likedByMe);
                })
                .collect(Collectors.toList());

        return PostListRs.builder()
                .data(data)
                .pagination(PostListRs.PaginationInfo.builder()
                        .page(page)
                        .limit(limit)
                        .total(postPage.getTotalElements())
                        .totalPages(postPage.getTotalPages())
                        .build())
                .build();
    }

    public PostDetailRs getPostDetail(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GlobalException("404", "게시글을 찾을 수 없습니다."));

        boolean likedByMe = postLikeRepository.existsByPostIdAndUserId(postId, userId);

        List<CommentDto> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(CommentDto::from)
                .collect(Collectors.toList());

        return PostDetailRs.from(post, likedByMe, comments);
    }

    @Transactional
    public Long createPost(Long userId, PostCreateRq request) {
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new GlobalException("404", "사용자를 찾을 수 없습니다."));

        Post post = Post.builder()
                .user(user)
                .floor(user.getFloor())
                .content(request.getContent())
                .build();

        postRepository.save(post);
        return post.getId();
    }

    @Transactional
    public void updatePost(Long postId, Long userId, PostUpdateRq request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GlobalException("404", "게시글을 찾을 수 없습니다."));

        if (!post.isOwnedBy(userId)) {
            throw new GlobalException("403", "본인이 작성한 게시글만 수정할 수 있습니다.");
        }

        post.updateContent(request.getContent());
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GlobalException("404", "게시글을 찾을 수 없습니다."));

        if (!post.isOwnedBy(userId)) {
            throw new GlobalException("403", "본인이 작성한 게시글만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }

    @Transactional
    public LikeRs toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GlobalException("404", "게시글을 찾을 수 없습니다."));

        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new GlobalException("404", "사용자를 찾을 수 없습니다."));

        var existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            post.decrementLikes();
            return LikeRs.of(false, post.getLikes());
        } else {
            PostLike like = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeRepository.save(like);
            post.incrementLikes();

            // 본인 게시글이 아닌 경우에만 알림 전송
            if (!post.isOwnedBy(userId)) {
                notificationService.notifyUser(
                        post.getUser().getId(),
                        "커뮤니티",
                        "내 게시글에 좋아요가 달렸습니다."
                );
            }

            return LikeRs.of(true, post.getLikes());
        }
    }

    @Transactional
    public Long createComment(Long postId, Long userId, CommentCreateRq request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GlobalException("404", "게시글을 찾을 수 없습니다."));

        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new GlobalException("404", "사용자를 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(request.getContent())
                .build();

        commentRepository.save(comment);

        // 본인 게시글이 아닌 경우에만 알림 전송
        if (!post.isOwnedBy(userId)) {
            notificationService.notifyUser(
                    post.getUser().getId(),
                    "커뮤니티",
                    "내 게시글에 댓글이 달렸습니다."
            );
        }

        return comment.getId();
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GlobalException("404", "댓글을 찾을 수 없습니다."));

        if (!comment.isOwnedBy(userId)) {
            throw new GlobalException("403", "본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }

    public NoticeListRs getNoticeList(Integer floor, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<CommunityNotice> notices = communityNoticeRepository.findByFloorOrGlobal(floor, pageable);

        List<NoticeDto> data = notices.stream()
                .map(NoticeDto::from)
                .collect(Collectors.toList());

        return NoticeListRs.of(data);
    }

    public NoticeDto getNoticeDetail(Long noticeId) {
        CommunityNotice notice = communityNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new GlobalException("404", "공지사항을 찾을 수 없습니다."));

        return NoticeDto.from(notice);
    }

    @Transactional
    public Long createNotice(Long adminId, NoticeCreateRq request) {
        Member admin = memberRepository.findById(adminId)
                .orElseThrow(() -> new GlobalException("404", "관리자를 찾을 수 없습니다."));

        if (!admin.isAdmin()) {
            throw new GlobalException("403", "관리자만 공지를 등록할 수 있습니다.");
        }

        CommunityNotice notice = CommunityNotice.builder()
                .admin(admin)
                .floor(request.getFloor())
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        communityNoticeRepository.save(notice);

        // 해당 층(또는 전체) 사용자에게 푸시 알림 전송
        notificationService.notifyFloorOrAll(
                request.getFloor(),
                "커뮤니티 공지",
                notice.getTitle()
        );

        return notice.getId();
    }
}
