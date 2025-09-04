package com.ahmadda.application;

import com.ahmadda.application.dto.EventCreateRequest;
import com.ahmadda.application.dto.EventCreated;
import com.ahmadda.application.dto.EventRead;
import com.ahmadda.application.dto.EventUpdateRequest;
import com.ahmadda.application.dto.EventUpdated;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.QuestionCreateRequest;
import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.event.GuestWithOptStatus;
import com.ahmadda.domain.event.Question;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.notification.EventNotificationOptOutRepository;
import com.ahmadda.domain.notification.Reminder;
import com.ahmadda.domain.notification.ReminderHistory;
import com.ahmadda.domain.notification.ReminderHistoryRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private static final int REMINDER_LIMIT_DURATION_MINUTES = 30;
    private static final int MAX_REMINDER_COUNT_IN_DURATION = 10;

    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final Reminder reminder;
    private final ReminderHistoryRepository reminderHistoryRepository;
    private final EventNotificationOptOutRepository eventNotificationOptOutRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Event createEvent(
            final Long organizationId,
            final LoginMember loginMember,
            final EventCreateRequest eventCreateRequest,
            final LocalDateTime currentDateTime
    ) {
        Organization organization = getOrganization(organizationId);
        OrganizationMember organizer = getOrganizationMember(organizationId, loginMember.memberId());

        EventOperationPeriod eventOperationPeriod = createEventOperationPeriod(eventCreateRequest, currentDateTime);
        Event event = Event.create(
                eventCreateRequest.title(),
                eventCreateRequest.description(),
                eventCreateRequest.place(),
                organizer,
                organization,
                eventOperationPeriod,
                eventCreateRequest.maxCapacity(),
                createQuestions(eventCreateRequest.questions())
        );
        validateReminderLimit(event);

        Event savedEvent = eventRepository.save(event);
        notifyEventCreated(savedEvent, organization);

        eventPublisher.publishEvent(EventCreated.from(savedEvent.getId()));

        return savedEvent;
    }

    @Transactional
    public void closeEventRegistration(
            final Long eventId,
            final Long memberId,
            final LocalDateTime currentDateTime
    ) {
        Event event = getEvent(eventId);
        Organization organization = event.getOrganization();
        OrganizationMember organizationMember = getOrganizationMember(organization.getId(), memberId);

        event.closeRegistrationAt(organizationMember, currentDateTime);
    }

    @Transactional
    public Event getOrganizationMemberEvent(final LoginMember loginMember, final Long eventId) {
        Event event = getEvent(eventId);

        Organization organization = event.getOrganization();

        validateOrganizationAccess(organization.getId(), loginMember.memberId());

        eventPublisher.publishEvent(EventRead.from(event, loginMember));

        return event;
    }

    @Transactional
    public Event updateEvent(
            final Long eventId,
            final LoginMember loginMember,
            final EventUpdateRequest eventUpdateRequest,
            final LocalDateTime currentDateTime
    ) {
        Event event = getEvent(eventId);
        Member member = getMember(loginMember.memberId());
        validateReminderLimit(event);

        EventOperationPeriod updatedOperationPeriod = EventOperationPeriod.create(
                event.getRegistrationStart(),
                eventUpdateRequest.registrationEnd(),
                eventUpdateRequest.eventStart(),
                eventUpdateRequest.eventEnd(),
                currentDateTime
        );
        event.update(
                member,
                eventUpdateRequest.title(),
                eventUpdateRequest.description(),
                eventUpdateRequest.place(),
                updatedOperationPeriod,
                eventUpdateRequest.maxCapacity()
        );

        notifyEventUpdated(event);

        eventPublisher.publishEvent(EventUpdated.from(event));

        return event;
    }

    public boolean isOrganizer(final Long eventId, final LoginMember loginMember) {
        Event event = getEvent(eventId);
        Member member = getMember(loginMember.memberId());

        return event.isOrganizer(member);
    }

    public List<Event> getPastEvent(
            final Long organizationId,
            final LoginMember loginMember,
            final LocalDateTime compareDateTime
    ) {
        validateOrganizationAccess(organizationId, loginMember.memberId());

        return eventRepository.findAllByEventOperationPeriodEventPeriodEndBefore(compareDateTime);
    }

    private Member getMember(final Long loginMember) {
        return memberRepository.findById(loginMember)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    private EventOperationPeriod createEventOperationPeriod(
            final EventCreateRequest eventCreateRequest,
            final LocalDateTime currentDateTime
    ) {

        return EventOperationPeriod.create(
                currentDateTime,
                eventCreateRequest.registrationEnd(),
                eventCreateRequest.eventStart(),
                eventCreateRequest.eventEnd(),
                currentDateTime
        );
    }

    private Event getEvent(final Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않은 이벤트 정보입니다."));
    }

    private Organization getOrganization(final Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않은 조직 정보입니다."));
    }

    private void validateOrganizationAccess(final Long organizationId, final Long memberId) {
        if (!organizationMemberRepository.existsByOrganizationIdAndMemberId(organizationId, memberId)) {
            throw new ForbiddenException("이벤트 스페이스에 소속되지 않아 권한이 없습니다.");
        }
    }

    private OrganizationMember getOrganizationMember(final Long organizationId, final Long memberId) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, memberId)
                .orElseThrow(() -> new NotFoundException("조직원을 찾을 수 없습니다."));
    }

    private List<Question> createQuestions(final List<QuestionCreateRequest> questionCreateRequests) {
        return IntStream.range(0, questionCreateRequests.size())
                .mapToObj(i -> {
                    QuestionCreateRequest request = questionCreateRequests.get(i);
                    return Question.create(request.questionText(), request.isRequired(), i);
                })
                .toList();
    }

    private void validateReminderLimit(final Event event) {
        LocalDateTime now = LocalDateTime.now();
        Long organizerId = event.getOrganizer()
                .getId();
        LocalDateTime threshold = now.minusMinutes(REMINDER_LIMIT_DURATION_MINUTES);

        List<ReminderHistory> recentReminderHistories = getRecentReminderHistories(organizerId, threshold);

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

    private List<ReminderHistory> getRecentReminderHistories(final Long organizerId, final LocalDateTime threshold) {
        return reminderHistoryRepository
                .findTop10ByEventOrganizerIdAndCreatedAtAfterOrderByCreatedAtDesc(organizerId, threshold);
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

    private void notifyEventCreated(final Event event, final Organization organization) {
        String content = "새로운 이벤트가 등록되었습니다.";
        List<OrganizationMember> recipients =
                event.getNonGuestOrganizationMembers(organization.getOrganizationMembers());

        sendAndRecordReminder(event, recipients, content);
    }

    private void notifyEventUpdated(final Event event) {
        String content = "이벤트 정보가 수정되었습니다.";
        List<GuestWithOptStatus> guestsWithOptOut = event.getGuests()
                .stream()
                .map(guest -> GuestWithOptStatus.createWithOptOutStatus(guest, eventNotificationOptOutRepository))
                .toList();
        List<OrganizationMember> recipients = GuestWithOptStatus.extractOptInOrganizationMembers(guestsWithOptOut);

        sendAndRecordReminder(event, recipients, content);
    }

    private void sendAndRecordReminder(
            final Event event,
            final List<OrganizationMember> recipients,
            final String content
    ) {
        ReminderHistory reminderHistory = reminder.remind(recipients, event, content);
        reminderHistoryRepository.save(reminderHistory);
    }
}
