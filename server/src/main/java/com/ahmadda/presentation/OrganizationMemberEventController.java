package com.ahmadda.presentation;


import com.ahmadda.application.OrganizationMemberService;
import com.ahmadda.domain.Event;
import com.ahmadda.presentation.dto.EventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/organization-members")
@RequiredArgsConstructor
public class OrganizationMemberEventController {

    private final OrganizationMemberService organizationMemberService;

    @GetMapping("/{organizationMemberId}/owned-events")
    public ResponseEntity<List<EventResponse>> getOwnerEvents(@PathVariable final Long organizationMemberId) {
        List<Event> organizationEvents = organizationMemberService.getOwnerEvents(organizationMemberId);

        List<EventResponse> eventResponses = organizationEvents.stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }

    @GetMapping("/{organizationMemberId}/participated-events")
    public ResponseEntity<List<EventResponse>> getParticipantEvents(@PathVariable final Long organizationMemberId) {
        List<Event> organizationEvents = organizationMemberService.getParticipantEvents(organizationMemberId);

        List<EventResponse> eventResponses = organizationEvents.stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }
}
