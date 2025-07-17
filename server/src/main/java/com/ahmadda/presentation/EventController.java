package com.ahmadda.presentation;


import com.ahmadda.application.EventService;
import com.ahmadda.application.GuestService;
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
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final GuestService guestService;

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<EventResponse>> getOrganizerEvent(@PathVariable final Long organizationId) {
        List<Event> organizerAvailableEvents = eventService.getOrganizationAvailableEvents(organizationId);
        List<EventResponse> eventResponses = organizerAvailableEvents.stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }

    @GetMapping("/organization/{organizationId}/owner/{memberId}")
    public ResponseEntity<List<EventResponse>> getOwnersEvent(@PathVariable final Long memberId,
                                                              @PathVariable final Long organizationId) {
        List<Event> ownerEvents = eventService.getOwnersEvent(memberId, organizationId);

        List<EventResponse> eventResponses = ownerEvents.stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }

    @GetMapping("/organization/{organizationId}/guest/{memberId}")
    public ResponseEntity<List<EventResponse>> getJoinedEvents(@PathVariable final Long memberId,
                                                               @PathVariable final Long organizationId) {
        List<Event> joinedEvents = guestService.getJoinedEvents(memberId, organizationId);

        List<EventResponse> eventResponses = joinedEvents.stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }
}
