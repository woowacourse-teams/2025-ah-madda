package com.ahmadda.presentation;

import com.ahmadda.application.EventNotificationService;
import com.ahmadda.application.dto.NotificationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventNotificationController {

    private final EventNotificationService eventNotificationService;

    @PostMapping("/{eventId}/notify-non-guests")
    public ResponseEntity<Void> notifyNonGuests(
            @PathVariable final Long eventId,
            @RequestBody @Valid final NotificationRequest request
    ) {
        eventNotificationService.notifyNonGuestOrganizationMembers(eventId, request.content());

        return ResponseEntity.ok()
                .build();
    }
}
