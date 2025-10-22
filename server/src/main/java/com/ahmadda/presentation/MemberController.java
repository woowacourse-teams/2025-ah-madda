package com.ahmadda.presentation;

import com.ahmadda.application.MemberService;
import com.ahmadda.application.OpenProfileService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OpenProfileUpdateRequest;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.OpenProfile;
import com.ahmadda.presentation.dto.MemberResponse;
import com.ahmadda.presentation.dto.OpenProfileResponse;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final OpenProfileService openProfileService;

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

    @Operation(summary = "자신의 오픈 프로필 조회", description = "로그인한 사용자의 오픈 프로필 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = OpenProfileResponse.class)
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
                            examples = {
                                    @ExampleObject(
                                            name = "회원이 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 회원입니다.",
                                                      "instance": "/api/members/profile"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈 프로필이 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 프로필입니다.",
                                                      "instance": "/api/members/profile"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping("/profile")
    public ResponseEntity<OpenProfileResponse> getOpenProfile(@Auth final LoginMember loginMember) {
        OpenProfile openProfile = openProfileService.getOpenProfile(loginMember);

        OpenProfileResponse response = OpenProfileResponse.from(openProfile);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "오픈 프로필 업데이트", description = "오픈 프로필의 닉네임과 그룹을 업데이트하고, 관련된 모든 조직 구성원 정보도 함께 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "오픈 프로필이 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 프로필입니다.",
                                                      "instance": "/api/members/profile"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "그룹이 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 그룹입니다.",
                                                      "instance": "/api/members/profile"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(
            @Auth final LoginMember loginMember,
            @Valid @RequestBody final OpenProfileUpdateRequest request
    ) {
        openProfileService.updateProfile(loginMember, request);

        return ResponseEntity.noContent()
                .build();
    }
}
