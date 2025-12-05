package com.startup.campusmate.domain.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateRq {

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 1, max = 500, message = "내용은 1~500자 사이로 입력해주세요.")
    private String content;
}
