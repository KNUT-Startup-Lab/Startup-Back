package com.startup.campusmate.domain.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateRq {

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    @Size(min = 1, max = 200, message = "댓글은 1~200자 사이로 입력해주세요.")
    private String content;
}
