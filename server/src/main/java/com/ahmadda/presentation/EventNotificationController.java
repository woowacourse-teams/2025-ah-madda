package com.ahmadda.presentation;

import com.ahmadda.application.EventNotificationService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.NotificationRequest;
import com.ahmadda.presentation.resolver.AuthMember;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Event Notification", description = "이벤트 알림 관련 API")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventNotificationController {

    private final EventNotificationService eventNotificationService;

    @PostMapping("/{eventId}/notify-non-guests")
    public ResponseEntity<Void> notifyNonGuests(
            @PathVariable final Long eventId,
            @RequestBody @Valid final NotificationRequest request,
            @AuthMember final LoginMember loginMember
    ) {
        eventNotificationService.notifyNonGuestOrganizationMembers(eventId, request, loginMember);

        return ResponseEntity.ok()
                .build();
    }
}
