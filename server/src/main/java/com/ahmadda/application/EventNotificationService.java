package com.ahmadda.application;

import com.ahmadda.application.dto.NotificationRequest;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.NotificationMailer;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.presentation.dto.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventNotificationService {

    private final EventRepository eventRepository;
    private final NotificationMailer notificationMailer;

    public void notifyNonGuestOrganizationMembers(
            final Long eventId,
            final NotificationRequest request,
            final LoginMember loginMember
    ) {
        Event event = getEvent(eventId);
        validateOrganizer(event, loginMember.memberId());
        List<OrganizationMember> organizationMembers = event.getOrganization()
                .getOrganizationMembers();

        List<OrganizationMember> recipients = event.getNonGuestOrganizationMembers(organizationMembers);
        String subject = generateSubject(event);

        sendNotificationToRecipients(recipients, subject, request.content());
    }

    private Event getEvent(final Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다."));
    }

    private void validateOrganizer(final Event event, final Long memberId) {
        if (!event.isOrganizer(memberId)) {
            throw new AccessDeniedException("이벤트 주최자가 아닙니다.");
        }
    }

    private String generateSubject(final Event event) {
        String organizationName = event.getOrganization()
                .getName();
        String organizerName = event.getOrganizer()
                .getNickname();
        String eventTitle = event.getTitle();

        return String.format("[%s] %s님의 이벤트 안내: %s", organizationName, organizerName, eventTitle);
    }

    private void sendNotificationToRecipients(
            final List<OrganizationMember> recipients,
            final String subject,
            final String content
    ) {
        recipients.forEach(recipient ->
                notificationMailer.sendNotification(
                        recipient.getMember()
                                .getEmail(),
                        subject,
                        // TODO. 템플릿을 이용하여 content 생성하는 로직으로 변경 필요
                        content
                )
        );
    }
}
