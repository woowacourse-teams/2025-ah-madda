package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.SelectedOrganizationMembersNotificationRequest;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.Reminder;
import com.ahmadda.domain.ReminderHistory;
import com.ahmadda.domain.ReminderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventNotificationService {

    private final Reminder reminder;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final ReminderHistoryRepository reminderHistoryRepository;
    
    public void notifySelectedOrganizationMembers(
            final Long eventId,
            final SelectedOrganizationMembersNotificationRequest request,
            final LoginMember loginMember
    ) {
        Event event = getEvent(eventId);
        validateOrganizer(event, loginMember.memberId());

        List<OrganizationMember> recipients = getEventRecipientsFromIds(event, request.organizationMemberIds());
        sendAndRecordReminder(recipients, event, request.content());
    }

    private Event getEvent(final Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다."));
    }

    private void validateOrganizer(final Event event, final Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        if (!event.isOrganizer(member)) {
            throw new AccessDeniedException("이벤트 주최자가 아닙니다.");
        }
    }

    private List<OrganizationMember> getEventRecipientsFromIds(
            final Event event,
            final List<Long> organizationMemberIds
    ) {
        Map<Long, OrganizationMember> organizationMembersById = getOrganizationMembersById(event.getOrganization());
        validateOrganizationMemberIdsExist(organizationMembersById, organizationMemberIds);

        return organizationMemberIds.stream()
                .map(organizationMembersById::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private Map<Long, OrganizationMember> getOrganizationMembersById(final Organization organization) {
        return organization
                .getOrganizationMembers()
                .stream()
                .collect(Collectors.toMap(OrganizationMember::getId, Function.identity()));
    }

    private void validateOrganizationMemberIdsExist(
            final Map<Long, OrganizationMember> organizationMembersById,
            final List<Long> requestedIds
    ) {
        boolean allExist = requestedIds.stream()
                .allMatch(organizationMembersById::containsKey);

        if (!allExist) {
            throw new NotFoundException("존재하지 않는 조직원입니다.");
        }
    }

    private void sendAndRecordReminder(
            final List<OrganizationMember> recipients,
            final Event event,
            final String request
    ) {
        ReminderHistory reminderHistory = reminder.remind(recipients, event, request);
        reminderHistoryRepository.save(reminderHistory);
    }
}
