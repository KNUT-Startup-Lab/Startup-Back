package com.startup.campusmate.domain.member.auth.dto.recovery;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindEmailRq {
    private String nickname;
    private String phoneNum;
}