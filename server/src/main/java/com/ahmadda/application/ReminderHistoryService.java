package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.notification.ReminderHistory;
import com.ahmadda.domain.notification.ReminderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderHistoryService {

    private final EventRepository eventRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final ReminderHistoryRepository reminderHistoryRepository;

    public List<ReminderHistory> getNotifyHistory(
            final Long eventId,
            final LoginMember loginMember
    ) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다."));
        OrganizationMember organizationMember = organizationMemberRepository
                .findByOrganizationIdAndMemberId(
                        event.getOrganization()
                                .getId(), loginMember.memberId()
                )
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직원 정보입니다."));

        validateIsOrganizer(event, organizationMember);

        return reminderHistoryRepository.findByEventId(eventId);
    }

    private void validateIsOrganizer(final Event event, final OrganizationMember organizationMember) {
        if (!event.isOrganizer(organizationMember)) {
            throw new AccessDeniedException("리마인더 히스토리는 이벤트의 주최자만 조회할 수 있습니다.");
        }
    }
}
