package com.startup.campusmate.domain.qna.controller;

import com.startup.campusmate.domain.qna.dto.*;
import com.startup.campusmate.domain.qna.service.QnaService;
import com.startup.campusmate.global.rsData.RsData;
import com.startup.campusmate.global.security.SecurityUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    @GetMapping
    public RsData<QnaListRs> getQnaList(
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int limit
    ) {
        QnaListRs result = qnaService.getQnaList(status, category, page, limit);
        return RsData.of("200-1", "Q&A 목록 조회 성공", result);
    }

    @GetMapping("/my")
    public RsData<QnaListRs> getMyQnaList(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int limit
    ) {
        QnaListRs result = qnaService.getMyQnaList(user.getId(), status, page, limit);
        return RsData.of("200-1", "내 Q&A 목록 조회 성공", result);
    }

    @GetMapping("/{id}")
    public RsData<QnaDto> getQnaDetail(@PathVariable Long id) {
        QnaDto result = qnaService.getQnaDetail(id);
        return RsData.of("200-1", "Q&A 상세 조회 성공", result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<QnaCreateRs> createQna(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody QnaCreateRq request
    ) {
        QnaCreateRs result = qnaService.createQna(user.getId(), request);
        return RsData.of("201-1", result.getMessage(), result);
    }

    @DeleteMapping("/{id}")
    public RsData<String> deleteQna(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long id
    ) {
        qnaService.deleteQna(id, user.getId());
        return RsData.of("200-1", "Q&A가 삭제되었습니다.");
    }

    @PostMapping("/{id}/answer")
    public RsData<String> addAnswer(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long id,
            @Valid @RequestBody QnaAnswerRq request
    ) {
        qnaService.addAnswer(id, user.getId(), request);
        return RsData.of("200-1", "답변이 등록되었습니다.");
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RsData<QnaUploadRs> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {
        QnaUploadRs result = qnaService.uploadImage(file);
        return RsData.of("200-1", "이미지 업로드 성공", result);
    }
}
