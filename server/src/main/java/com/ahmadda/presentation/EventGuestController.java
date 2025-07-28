package com.ahmadda.presentation;

import com.ahmadda.application.EventGuestService;
import com.ahmadda.application.dto.EventParticipateRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.Guest;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.presentation.dto.GuestResponse;
import com.ahmadda.presentation.dto.GuestStatusResponse;
import com.ahmadda.presentation.dto.OrganizationMemberResponse;
import com.ahmadda.presentation.resolver.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Event Guest", description = "이벤트 게스트 관련 API")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventGuestController {

    private final EventGuestService eventGuestService;

    @Operation(summary = "이벤트 게스트 목록 조회", description = "해당 이벤트에 참여한 게스트 목록을 조회합니다. 주최자만 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(
                                    implementation = GuestResponse.class
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
                                              "detail": "유효하지 않은 인증 정보 입니다.",
                                              "instance": "/api/events/{eventId}/guests"
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
                                              "detail": "존재하지 않는 이벤트입니다.",
                                              "instance": "/api/events/{eventId}/guests"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Forbidden",
                                              "status": 403,
                                              "detail": "이벤트 주최자가 아닙니다.",
                                              "instance": "/api/events/{eventId}/guests"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{eventId}/guests")
    public ResponseEntity<List<GuestResponse>> getGuests(
            @PathVariable final Long eventId,
            @AuthMember final LoginMember loginMember
    ) {
        List<Guest> guestMembers = eventGuestService.getGuests(eventId, loginMember);

        List<GuestResponse> responses = guestMembers.stream()
                .map(GuestResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "이벤트 미참여 조직원 목록 조회", description = "해당 이벤트에 아직 참여하지 않은 조직원 목록을 조회합니다. 주최자만 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(
                                    implementation = OrganizationMemberResponse.class
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
                                              "detail": "유효하지 않은 인증 정보 입니다.",
                                              "instance": "/api/events/{eventId}/non-guests"
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
                                              "detail": "존재하지 않는 이벤트입니다.",
                                              "instance": "/api/events/{eventId}/non-guests"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Forbidden",
                                              "status": 403,
                                              "detail": "이벤트 주최자가 아닙니다.",
                                              "instance": "/api/events/{eventId}/non-guests"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{eventId}/non-guests")
    public ResponseEntity<List<OrganizationMemberResponse>> getNonGuests(
            @PathVariable final Long eventId,
            @AuthMember final LoginMember loginMember
    ) {
        List<OrganizationMember> nonGuestMembers =
                eventGuestService.getNonGuestOrganizationMembers(eventId, loginMember);

        List<OrganizationMemberResponse> responses = nonGuestMembers.stream()
                .map(OrganizationMemberResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{eventId}/participation")
    public ResponseEntity<Void> participateEvent(
            @PathVariable final Long eventId,
            @RequestBody @Valid final EventParticipateRequest eventParticipateRequest,
            @AuthMember final LoginMember loginMember
    ) {
        eventGuestService.participantEvent(
                eventId,
                loginMember.memberId(),
                LocalDateTime.now(),
                eventParticipateRequest
        );

        return ResponseEntity.ok()
                .build();
    }

    @GetMapping("/{eventId}/guest-status")
    public ResponseEntity<GuestStatusResponse> isGuest(
            @PathVariable final Long eventId,
            @AuthMember final LoginMember loginMember
    ) {
        boolean isGuest = eventGuestService.isGuest(eventId, loginMember.memberId());

        return ResponseEntity.ok(new GuestStatusResponse(isGuest));
    }
}
