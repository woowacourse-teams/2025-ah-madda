package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.NonGuestsNotificationRequest;
import com.ahmadda.application.dto.SelectedOrganizationMembersNotificationRequest;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.NotificationMailer;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
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

    private final EventRepository eventRepository;
    private final NotificationMailer notificationMailer;
    private final MemberRepository memberRepository;

    public void notifyNonGuestOrganizationMembers(
            final Long eventId,
            final NonGuestsNotificationRequest request,
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

    public void notifySelectedOrganizationMembers(
            final Long eventId,
            final SelectedOrganizationMembersNotificationRequest request,
            final LoginMember loginMember
    ) {
        Event event = getEvent(eventId);
        validateOrganizer(event, loginMember.memberId());

        List<OrganizationMember> recipients = getEventRecipientsFromIds(event, request.organizationMemberIds());
        String subject = generateSubject(event);

        sendNotificationToRecipients(recipients, subject, request.content());
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
