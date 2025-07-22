package com.ahmadda.presentation;

import com.ahmadda.application.EventGuestService;
import com.ahmadda.application.dto.EventParticipateRequest;
import com.ahmadda.domain.Guest;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.presentation.dto.GuestResponse;
import com.ahmadda.presentation.dto.LoginMember;
import com.ahmadda.presentation.dto.OrganizationMemberResponse;
import com.ahmadda.presentation.resolver.AuthMember;
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

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventGuestController {

    private final EventGuestService eventGuestService;

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
}
