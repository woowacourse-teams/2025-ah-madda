package com.ahmadda.presentation;

import com.ahmadda.application.MemberService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.Member;
import com.ahmadda.presentation.dto.MemberResponse;
import com.ahmadda.presentation.resolver.AuthMember;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/profile")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            schema = @Schema(
                                    implementation = MemberResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않는 회원입니다.",
                                              "instance": "/api/members/profile"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<MemberResponse> getMemberProfile(@AuthMember final LoginMember loginMember) {
        Member member = memberService.getMember(loginMember);

        MemberResponse response = MemberResponse.from(member);

        return ResponseEntity.ok(response);
    }
}
