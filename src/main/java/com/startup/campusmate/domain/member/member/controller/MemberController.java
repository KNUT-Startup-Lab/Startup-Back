package com.startup.campusmate.domain.member.member.controller;

import com.startup.campusmate.domain.member.auth.dto.recovery.ChangePassword;
import com.startup.campusmate.domain.member.member.dto.DeleteRequest;
import com.startup.campusmate.domain.member.member.dto.MemberDto;
import com.startup.campusmate.domain.member.member.dto.SignupRq;
import com.startup.campusmate.domain.member.member.entity.Member;
import com.startup.campusmate.domain.member.member.service.MemberService;
import com.startup.campusmate.global.exceptions.GlobalException;
import com.startup.campusmate.global.rsData.RsData;
import com.startup.campusmate.standard.base.Empty;
import com.startup.campusmate.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("")
    public ResponseEntity<RsData<Empty>> signup(@RequestBody SignupRq signupRq) {
        if ( Ut.str.isBlank(signupRq.getUsername()) ) {
            throw new GlobalException("400-1", "이메일 공백은 지원하지 않습니다.");
        }
        if ( Ut.str.isBlank(signupRq.getStudentNum()) ) {
            throw new GlobalException("400-1", "학번 공백은 지원하지 않습니다.");
        }
        if ( Ut.str.isBlank(signupRq.getPassword()) ) {
            throw new GlobalException("400-1", "비밀번호 공백은 지원하지 않습니다.");
        }
        memberService.signup(signupRq);
        return ResponseEntity.ok(RsData.of("회원가입이 완료되었습니다."));
    }

    @PutMapping("/password")
    public ResponseEntity<RsData<Empty>> changePassword(@RequestBody ChangePassword changePassword) {
        // 저장소에서 변경하는 코드
        memberService.changePassword(
                changePassword.getCurrentPassword(),
                changePassword.getNewPassword()
        );
        return ResponseEntity.ok(RsData.of("비밀번호 변경 성공"));
    }

    @GetMapping("/check-email")
    public ResponseEntity<RsData<Boolean>> checkEmail(@RequestParam("email") String email) {
        Boolean isAvailable = memberService.isEmailAvailable(email);
        return ResponseEntity.ok(RsData.of("사용 가능한 이메일", isAvailable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RsData<MemberDto>> getMemberById(@PathVariable("id") Long id) {
        // memberService를 통해 id에 해당하는 회원을 찾습니다.
        // findById는 Optional<Member>를 반환하므로, 없을 경우 예외 처리를 해주는 것이 좋습니다.
        Member member = memberService.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 ID의 회원을 찾을 수 없습니다."));

        MemberDto memberDto = MemberDto.from(member);

        return ResponseEntity.ok(RsData.of("%d번 회원을 성공적으로 조회했습니다.".formatted(id), memberDto));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<RsData<MemberDto>> modifyProfile(
            @PathVariable("id") Long id,
            @RequestBody MemberDto memberDto) {

        // 현재 로그인한 사용자가 자신의 프로필을 수정하는지, 또는 관리자인지 확인
        // Rq 객체나 @PreAuthorize("#id == @rq.member.id or hasRole('ADMIN')") 등으로 권한 검사를 수행하는 것이 안전합니다.
        Member updatedMember = memberService.modify(id, memberDto);

        MemberDto updatedDto = MemberDto.from(updatedMember);

        return ResponseEntity.ok(RsData.of("프로필이 성공적으로 수정되었습니다.", updatedDto));
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<RsData<String>> changeImage(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file) {

        // 서비스를 호출하여 이미지 업로드 및 회원 정보 업데이트
        String imageUrl = memberService.updateProfileImage(id, file);

        return ResponseEntity.ok(RsData.of("프로필 이미지가 성공적으로 변경되었습니다.", imageUrl));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}/image")
    public RsData<?> deleteImage(@PathVariable("id") Long id) {
        memberService.deleteProfileImage(id);

        return RsData.of("S-3", "프로필 이미지가 성공적으로 삭제되었습니다.");
    }

    @DeleteMapping("/{id}/delete")
    public RsData<?> deleteAccount(@PathVariable("id") Long id, @RequestBody DeleteRequest deleteRequest) {
        // 서비스를 호출하여 비밀번호 검증 및 회원 삭제 처리
        memberService.deleteAccount(id, deleteRequest.password());

        // 💡 실제로는 여기서 세션을 무효화하거나 JWT 토큰을 블랙리스트 처리하는 로직이 필요합니다.

        return RsData.of("S-4", "성공적으로 회원 탈퇴가 처리되었습니다.");
    }

}
