package com.ahmadda.presentation;

import com.ahmadda.application.MemberService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.member.Member;
import com.ahmadda.presentation.dto.MemberResponse;
import com.ahmadda.presentation.dto.OwnerEventResponse;
import com.ahmadda.presentation.dto.ParticipatedEventResponse;
import com.ahmadda.presentation.resolver.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "자신의 프로필 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(
                                    implementation = MemberResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/members/profile"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
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
    @GetMapping("/profile")
    public ResponseEntity<MemberResponse> getMemberProfile(@Auth final LoginMember loginMember) {
        Member member = memberService.getMember(loginMember);

        MemberResponse response = MemberResponse.from(member);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내가 참가한 이벤트 목록 조회", description = "로그인한 사용자가 참가한 이벤트 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ParticipatedEventResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/events/participated"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않는 회원입니다.",
                                              "instance": "/api/events/participated"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/events/participated")
    public ResponseEntity<List<ParticipatedEventResponse>> getParticipantEvents(
            @Auth final LoginMember loginMember
    ) {
        List<Event> organizationEvents =
                memberService.getParticipatedEvents(loginMember);

        List<ParticipatedEventResponse> eventResponses = organizationEvents.stream()
                .map(ParticipatedEventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }

    @Operation(summary = "내가 주최한 이벤트 목록 조회", description = "로그인한 사용자가 주최한 이벤트 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = OwnerEventResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/events/owned"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않는 회원입니다.",
                                              "instance": "/api/events/owned"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/events/owned")
    public ResponseEntity<List<OwnerEventResponse>> getOwnerEvents(
            @Auth final LoginMember loginMember
    ) {
        List<Event> organizationEvents = memberService.getOwnerEvents(loginMember);

        List<OwnerEventResponse> eventResponses = organizationEvents.stream()
                .map(OwnerEventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }

}
