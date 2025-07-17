package com.ahmadda.presentation;


import com.ahmadda.application.OrganizationMemberService;
import com.ahmadda.application.dto.EventResponse;
import com.ahmadda.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/organization-member")
@RequiredArgsConstructor
public class OrganizationMemberController {

    private final OrganizationMemberService organizationMemberService;

    @GetMapping("/{organizationMemberId}/event-owner")
    public ResponseEntity<List<EventResponse>> getOwnerEvents(@PathVariable final Long organizationMemberId) {
        List<Event> organizationEvents = organizationMemberService.getOwnerEvents(organizationMemberId);

        List<EventResponse> eventResponses = organizationEvents.stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }

    @GetMapping("/{organizationMemberId}/event-participant")
    public ResponseEntity<List<EventResponse>> getParticipantEvents(@PathVariable final Long organizationMemberId) {
        List<Event> organizationEvents = organizationMemberService.getParticipantEvents(organizationMemberId);

        List<EventResponse> eventResponses = organizationEvents.stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }
}
