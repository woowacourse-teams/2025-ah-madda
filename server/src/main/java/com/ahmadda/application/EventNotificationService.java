package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.SelectedOrganizationMembersNotificationRequest;
import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.notification.EventNotificationOptOutRepository;
import com.ahmadda.domain.notification.Reminder;
import com.ahmadda.domain.notification.ReminderHistory;
import com.ahmadda.domain.notification.ReminderHistoryRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberWithOptStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventNotificationService {

    private static final int REMINDER_LIMIT_DURATION_MINUTES = 30;
    private static final int MAX_REMINDER_COUNT_IN_DURATION = 10;

    private final Reminder reminder;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final EventNotificationOptOutRepository eventNotificationOptOutRepository;
    private final ReminderHistoryRepository reminderHistoryRepository;

    @Transactional(readOnly = true)
    public void notifySelectedOrganizationMembers(
            final Long eventId,
            final SelectedOrganizationMembersNotificationRequest request,
            final LoginMember loginMember
    ) {
        Event event = getEvent(eventId);
        validateOrganizer(event, loginMember.memberId());
        validateContentLength(request.content());
        validateReminderLimit(event);

        List<OrganizationMember> recipients =
                getOrganizationMemberFromIds(event, request.organizationMemberIds());
        validateRecipientsOptIn(recipients, event);

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
            throw new ForbiddenException("이벤트 주최자가 아닙니다.");
        }
    }

    private void validateContentLength(final String content) {
        if (content.length() > 20) {
            throw new UnprocessableEntityException("알림 메시지는 20자 이하여야 합니다.");
        }
    }

    private void validateReminderLimit(final Event event) {
        LocalDateTime now = LocalDateTime.now();

        Long eventId = event.getId();
        LocalDateTime threshold = now.minusMinutes(REMINDER_LIMIT_DURATION_MINUTES);

        List<ReminderHistory> recentReminderHistories = getRecentReminderHistories(eventId, threshold);

        if (recentReminderHistories.size() >= MAX_REMINDER_COUNT_IN_DURATION) {
            LocalDateTime oldestReminderTime = recentReminderHistories
                    .get(MAX_REMINDER_COUNT_IN_DURATION - 1)
                    .getCreatedAt();

            long minutesUntilAvailable = calculateRemainingMinutes(now, oldestReminderTime);

            throw new UnprocessableEntityException(
                    String.format(
                            "리마인더는 %d분 내 최대 %d회까지만 발송할 수 있습니다. 약 %d분 후 다시 시도해주세요.",
                            REMINDER_LIMIT_DURATION_MINUTES,
                            MAX_REMINDER_COUNT_IN_DURATION,
                            minutesUntilAvailable
                    )
            );
        }
    }

    private List<ReminderHistory> getRecentReminderHistories(final Long eventId, final LocalDateTime threshold) {
        return reminderHistoryRepository
                .findTop10ByEventIdAndCreatedAtAfterOrderByCreatedAtDesc(eventId, threshold);
    }

    /**
     * 남은 대기 시간을 분 단위로 계산한다.
     * <p>
     * 사유: Duration의 toMinutes()는 내림 처리되므로,
     * 예외 메시지에 "0분"으로 표시되는 오차를 방지하기 위해 초 단위로 계산 후 올림 처리한다.
     *
     * @param now                현재 시각
     * @param oldestReminderTime 제한 기준이 되는 리마인더의 시각
     * @return 제한 해제까지 남은 시간 (분 단위, 올림 처리)
     */
    private long calculateRemainingMinutes(final LocalDateTime now, final LocalDateTime oldestReminderTime) {
        Duration remaining = Duration.between(now, oldestReminderTime.plusMinutes(REMINDER_LIMIT_DURATION_MINUTES));

        return Math.max(0, (remaining.getSeconds() + 59) / 60);
    }

    private List<OrganizationMember> getOrganizationMemberFromIds(
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
            throw new NotFoundException("존재하지 않는 구성원입니다.");
        }
    }

    private void validateRecipientsOptIn(
            final List<OrganizationMember> organizationMembers,
            final Event event
    ) {
        boolean hasOptOut = organizationMembers.stream()
                .map(organizationMember -> OrganizationMemberWithOptStatus.createWithOptOutStatus(
                        organizationMember,
                        event,
                        eventNotificationOptOutRepository
                ))
                .anyMatch(OrganizationMemberWithOptStatus::isOptedOut);

        if (hasOptOut) {
            throw new UnprocessableEntityException("선택된 구성원 중 알림 수신 거부자가 존재합니다.");
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
