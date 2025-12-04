package com.startup.campusmate.domain.qna.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnaAnswerRq {

    @NotBlank(message = "답변을 입력해주세요.")
    private String answer;
}
