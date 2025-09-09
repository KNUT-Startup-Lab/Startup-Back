package com.startup.campusmate.domain.member.member.dto;

import com.startup.campusmate.domain.member.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
    private String username;
    private String password;
    private String nickname;
    private String studentNum;
    private String college;
    private boolean _isAdmin;

    public static MemberDto from(Member member) {
        // Member 객체가 null이면 null을 반환하거나 예외를 던질 수 있습니다.
        if (member == null) return null;

        // 빌더 패턴을 사용하여 MemberDto 객체를 생성하고 반환합니다.
        return MemberDto.builder()
                .username(member.getUsername())
                .password(member.getPassword()) // 🚨 이 부분은 아래 보안 경고를 꼭 확인하세요.
                .nickname(member.getNickname())
                .studentNum(member.getStudentNum())
                .college(member.getCollege())
                ._isAdmin(member.isAdmin()) // boolean getter는 보통 isXXX() 형태입니다.
                .build();
    }
}
