package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.NonGuestsNotificationRequest;
import com.ahmadda.application.dto.SelectedOrganizationMembersNotificationRequest;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.EmailNotifier;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventEmailPayload;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.PushNotificationPayload;
import com.ahmadda.domain.PushNotifier;
import com.ahmadda.infra.notification.push.FcmRegistrationToken;
import com.ahmadda.infra.notification.push.FcmRegistrationTokenRepository;
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

    private final EmailNotifier emailNotifier;
    private final PushNotifier pushNotifier;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final FcmRegistrationTokenRepository fcmRegistrationTokenRepository;

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
        sendNotificationToRecipients(recipients, event, request.content());
    }

    public void notifySelectedOrganizationMembers(
            final Long eventId,
            final SelectedOrganizationMembersNotificationRequest request,
            final LoginMember loginMember
    ) {
        Event event = getEvent(eventId);
        validateOrganizer(event, loginMember.memberId());

        List<OrganizationMember> recipients = getEventRecipientsFromIds(event, request.organizationMemberIds());
        sendNotificationToRecipients(recipients, event, request.content());
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

    private void sendNotificationToRecipients(
            final List<OrganizationMember> recipients,
            final Event event,
            final String content
    ) {
        sendEmailsToRecipients(recipients, event, content);
        sendPushNotificationsToRecipients(recipients, event, content);
    }

    private void sendEmailsToRecipients(
            final List<OrganizationMember> recipients,
            final Event event,
            final String content
    ) {
        List<String> recipientEmails = recipients.stream()
                .map(organizationMember -> organizationMember.getMember()
                        .getEmail())
                .toList();
        EventEmailPayload eventEmailPayload = EventEmailPayload.of(event, content);

        emailNotifier.sendEmails(recipientEmails, eventEmailPayload);
    }

    private void sendPushNotificationsToRecipients(
            final List<OrganizationMember> recipients,
            final Event event,
            final String content
    ) {
        List<Long> memberIds = recipients.stream()
                .map(orgMember -> orgMember.getMember()
                        .getId())
                .toList();
        List<String> registrationTokens = fcmRegistrationTokenRepository.findAllByMemberIdIn(memberIds)
                .stream()
                .map(FcmRegistrationToken::getRegistrationToken)
                .toList();
        PushNotificationPayload pushNotificationPayload = PushNotificationPayload.of(event, content);

        pushNotifier.sendPushs(registrationTokens, pushNotificationPayload);
    }
}
