package com.ahmadda.presentation;

import com.ahmadda.application.EventGuestService;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.presentation.dto.OrganizationMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventGuestController {

    private final EventGuestService eventGuestService;

    @GetMapping("/{eventId}/guest-organization-members")
    public ResponseEntity<List<OrganizationMemberResponse>> getGuestOrganizationMembers(@PathVariable final Long eventId) {
        List<OrganizationMember> guestMembers = eventGuestService.getGuestOrganizationMembers(eventId);

        List<OrganizationMemberResponse> responses = guestMembers.stream()
                .map(OrganizationMemberResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{eventId}/non-guest-organization-members")
    public ResponseEntity<List<OrganizationMemberResponse>> getNonGuestOrganizationMembers(@PathVariable final Long eventId) {
        List<OrganizationMember> nonGuestMembers = eventGuestService.getNonGuestOrganizationMembers(eventId);

        List<OrganizationMemberResponse> responses = nonGuestMembers.stream()
                .map(OrganizationMemberResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }
}
