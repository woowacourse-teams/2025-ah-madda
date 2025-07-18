package com.ahmadda.presentation;


import com.ahmadda.application.OrganizationService;
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
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationEventController {

    private final OrganizationService organizationService;

    @GetMapping("/{organizationId}/events")
    public ResponseEntity<List<EventResponse>> getOrganizationEvents(@PathVariable final Long organizationId) {
        List<Event> organizationEvents = organizationService.getOrganizationEvents(organizationId);

        List<EventResponse> eventResponses = organizationEvents.stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(eventResponses);
    }
}
