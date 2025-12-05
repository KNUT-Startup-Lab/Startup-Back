package com.startup.campusmate.domain.qna.service;

import com.startup.campusmate.domain.member.member.entity.Member;
import com.startup.campusmate.domain.member.member.repository.MemberRepository;
import com.startup.campusmate.domain.push.service.NotificationService;
import com.startup.campusmate.domain.qna.dto.*;
import com.startup.campusmate.domain.qna.entity.Qna;
import com.startup.campusmate.domain.qna.entity.QnaCategory;
import com.startup.campusmate.domain.qna.entity.QnaImage;
import com.startup.campusmate.domain.qna.entity.QnaStatus;
import com.startup.campusmate.domain.qna.repository.QnaImageRepository;
import com.startup.campusmate.domain.qna.repository.QnaRepository;
import com.startup.campusmate.global.exceptions.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {

    private final QnaRepository qnaRepository;
    private final QnaImageRepository qnaImageRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    @Value("${custom.file.upload-dir:/uploads/qna/}")
    private String uploadDir;

    public QnaListRs getQnaList(String status, String category, int page, int limit) {
        QnaStatus qnaStatus = parseStatus(status);
        QnaCategory qnaCategory = parseCategory(category);
        Pageable pageable = PageRequest.of(page - 1, limit);

        Page<Qna> qnaPage = qnaRepository.findAllWithFilter(qnaStatus, qnaCategory, pageable);

        List<QnaDto> data = qnaPage.getContent().stream()
                .map(QnaDto::from)
                .collect(Collectors.toList());

        long total = qnaRepository.count();
        long pending = qnaRepository.countByStatus(QnaStatus.PENDING);
        long answered = qnaRepository.countByStatus(QnaStatus.ANSWERED);

        return QnaListRs.builder()
                .data(data)
                .pagination(QnaListRs.PaginationInfo.builder()
                        .page(page)
                        .limit(limit)
                        .total(qnaPage.getTotalElements())
                        .totalPages(qnaPage.getTotalPages())
                        .build())
                .stats(QnaListRs.QnaStats.builder()
                        .total(total)
                        .pending(pending)
                        .answered(answered)
                        .build())
                .build();
    }

    public QnaListRs getMyQnaList(Long userId, String status, int page, int limit) {
        QnaStatus qnaStatus = parseStatus(status);
        Pageable pageable = PageRequest.of(page - 1, limit);

        Page<Qna> qnaPage = qnaRepository.findByUserIdWithFilter(userId, qnaStatus, pageable);

        List<QnaDto> data = qnaPage.getContent().stream()
                .map(QnaDto::from)
                .collect(Collectors.toList());

        long total = qnaRepository.countByUserId(userId);
        long pending = qnaRepository.countByUserIdAndStatus(userId, QnaStatus.PENDING);
        long answered = qnaRepository.countByUserIdAndStatus(userId, QnaStatus.ANSWERED);

        return QnaListRs.builder()
                .data(data)
                .pagination(QnaListRs.PaginationInfo.builder()
                        .page(page)
                        .limit(limit)
                        .total(qnaPage.getTotalElements())
                        .totalPages(qnaPage.getTotalPages())
                        .build())
                .stats(QnaListRs.QnaStats.builder()
                        .total(total)
                        .pending(pending)
                        .answered(answered)
                        .build())
                .build();
    }

    public QnaDto getQnaDetail(Long id) {
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new GlobalException("404", "Q&A를 찾을 수 없습니다."));
        return QnaDto.from(qna);
    }

    @Transactional
    public QnaCreateRs createQna(Long userId, QnaCreateRq request) {
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new GlobalException("404", "사용자를 찾을 수 없습니다."));

        QnaCategory category = QnaCategory.fromDisplayName(request.getCategory());

        Qna qna = Qna.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .category(category)
                .build();

        qnaRepository.save(qna);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (String imageUrl : request.getImages()) {
                QnaImage image = QnaImage.builder()
                        .imageUrl(imageUrl)
                        .build();
                qna.addImage(image);
            }
        }

        return QnaCreateRs.of(qna.getId(), "질문이 등록되었습니다.");
    }

    @Transactional
    public void deleteQna(Long qnaId, Long userId) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new GlobalException("404", "Q&A를 찾을 수 없습니다."));

        if (!qna.isOwnedBy(userId)) {
            throw new GlobalException("403", "본인이 작성한 Q&A만 삭제할 수 있습니다.");
        }

        if (!qna.isPending()) {
            throw new GlobalException("400", "답변이 달린 Q&A는 삭제할 수 없습니다.");
        }

        qnaRepository.delete(qna);
    }

    @Transactional
    public void addAnswer(Long qnaId, Long adminId, QnaAnswerRq request) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new GlobalException("404", "Q&A를 찾을 수 없습니다."));

        Member admin = memberRepository.findById(adminId)
                .orElseThrow(() -> new GlobalException("404", "관리자를 찾을 수 없습니다."));

        if (!admin.isAdmin()) {
            throw new GlobalException("403", "관리자만 답변을 등록할 수 있습니다.");
        }

        qna.addAnswer(request.getAnswer(), admin);

        // 질문 작성자에게 푸시 알림 전송
        notificationService.notifyWithData(
                qna.getUser().getId(),
                "Q&A 답변 등록",
                "\"" + qna.getTitle() + "\" 질문에 답변이 등록되었습니다.",
                "qna"
        );
    }

    @Transactional
    public QnaUploadRs uploadImage(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String storedFilename = UUID.randomUUID().toString() + extension;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(storedFilename);
            Files.write(filePath, file.getBytes());

            String fileUrl = "/uploads/qna/" + storedFilename;
            return QnaUploadRs.of(fileUrl);

        } catch (IOException e) {
            throw new GlobalException("500", "파일 업로드에 실패했습니다.");
        }
    }

    private QnaStatus parseStatus(String status) {
        if (status == null || status.isEmpty() || "all".equalsIgnoreCase(status)) {
            return null;
        }
        try {
            return QnaStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private QnaCategory parseCategory(String category) {
        if (category == null || category.isEmpty()) {
            return null;
        }
        return QnaCategory.fromDisplayName(category);
    }
}
