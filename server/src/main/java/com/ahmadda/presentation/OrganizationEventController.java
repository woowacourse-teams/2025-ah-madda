package com.ahmadda.presentation;


import com.ahmadda.application.EventService;
import com.ahmadda.application.OrganizationMemberEventService;
import com.ahmadda.application.OrganizationService;
import com.ahmadda.application.dto.EventCreateRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.Event;
import com.ahmadda.presentation.dto.EventCreateResponse;
import com.ahmadda.presentation.dto.EventDetailResponse;
import com.ahmadda.presentation.dto.EventResponse;
import com.ahmadda.presentation.dto.MyEventResponse;
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

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationEventController {

    private final OrganizationService organizationService;
    private final OrganizationMemberEventService organizationMemberEventService;
    private final EventService eventService;

    @GetMapping("/{organizationId}/events")
    public ResponseEntity<List<EventResponse>> getOrganizationEvents(@PathVariable final Long organizationId) {
        List<Event> organizationEvents = organizationService.getOrganizationEvents(organizationId);

        List<EventResponse> eventResponses = organizationEvents.stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }

    @PostMapping("/{organizationId}/events")
    public ResponseEntity<EventCreateResponse> createOrganizationEvent(
            @PathVariable final Long organizationId,
            @RequestBody @Valid final EventCreateRequest eventCreateRequest,
            @AuthMember final LoginMember loginMember
    ) {
        Event event = eventService.createEvent(organizationId, loginMember.memberId(), eventCreateRequest);

        return ResponseEntity.created(URI.create("/api/organizations/" + organizationId + "/events/" + event.getId()))
                .body(new EventCreateResponse(event.getId()));
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventDetailResponse> getOrganizationEvent(@PathVariable final Long eventId) {
        Event event = eventService.getEvent(eventId);

        return ResponseEntity.ok(EventDetailResponse.from(event));
    }

    @GetMapping("/{organizationId}/events/owned")
    public ResponseEntity<List<MyEventResponse>> getOwnerEvents(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember
    ) {
        List<Event> organizationEvents = organizationMemberEventService.getOwnerEvents(organizationId, loginMember);

        List<MyEventResponse> eventResponses = organizationEvents.stream()
                .map(event -> MyEventResponse.from(event, true, false))
                .toList();

        return ResponseEntity.ok(eventResponses);
    }

    @GetMapping("/{organizationId}/events/participated")
    public ResponseEntity<List<MyEventResponse>> getParticipantEvents(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember
    ) {
        List<Event> organizationEvents =
                organizationMemberEventService.getParticipantEvents(organizationId, loginMember);

        List<MyEventResponse> eventResponses = organizationEvents.stream()
                .map(event -> MyEventResponse.from(event, false, true))
                .toList();

        return ResponseEntity.ok(eventResponses);
    }
}
