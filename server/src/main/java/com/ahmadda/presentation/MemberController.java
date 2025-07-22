package com.ahmadda.presentation;

import com.ahmadda.application.MemberService;
import com.ahmadda.domain.Member;
import com.ahmadda.presentation.dto.LoginMember;
import com.ahmadda.presentation.dto.MemberResponse;
import com.ahmadda.presentation.resolver.AuthMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/profile")
    public ResponseEntity<MemberResponse> getMemberProfile(@AuthMember final LoginMember loginMember) {
        Member member = memberService.getMember(loginMember);
        
        MemberResponse response = MemberResponse.from(member);

        return ResponseEntity.ok(response);
    }
}
