package com.ahmadda.application.exception;


import com.ahmadda.application.EventResponse;
import com.ahmadda.application.EventService;
import com.ahmadda.application.GuestService;
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
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;
    private final GuestService guestService;

    @GetMapping("/{organizerId}")
    public ResponseEntity<List<EventResponse>> getOrganizerEvent(@PathVariable Long organizerId) {
        List<Event> organizerAvailableEvents = eventService.getOrganizerAvailableEvents(organizerId);
        List<EventResponse> eventResponses = organizerAvailableEvents
                .stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }

    @GetMapping("/my/owner/{organizerId}")
    public ResponseEntity<List<EventResponse>> getOwnersEvent(Long memberId, @PathVariable Long organizerId) {
        List<Event> ownerEvents = eventService.getOwnersEvent(memberId, organizerId);

        List<EventResponse> eventResponses = ownerEvents
                .stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }

    @GetMapping("/my/guest/{organizerId}")
    public ResponseEntity<List<EventResponse>> getJoinedEvents(Long memberId, @PathVariable Long organizerId) {
        List<Event> joinedEvents = guestService.getJoinedEvents(memberId, organizerId);

        List<EventResponse> eventResponses = joinedEvents
                .stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }
}
