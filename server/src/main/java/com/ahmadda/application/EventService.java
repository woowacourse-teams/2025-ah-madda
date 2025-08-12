package com.ahmadda.application;

import com.ahmadda.application.dto.EventCreateRequest;
import com.ahmadda.application.dto.EventCreated;
import com.ahmadda.application.dto.EventRead;
import com.ahmadda.application.dto.EventUpdateRequest;
import com.ahmadda.application.dto.EventUpdated;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.QuestionCreateRequest;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Guest;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.domain.Question;
import com.ahmadda.domain.Reminder;
import com.ahmadda.domain.ReminderHistory;
import com.ahmadda.domain.ReminderHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final Reminder reminder;
    private final ReminderHistoryRepository reminderHistoryRepository;
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
            throw new AccessDeniedException("조직에 소속되지 않아 권한이 없습니다.");
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

    private void notifyEventCreated(final Event event, final Organization organization) {
        String content = "새로운 이벤트가 등록되었습니다.";
        List<OrganizationMember> recipients =
                event.getNonGuestOrganizationMembers(organization.getOrganizationMembers());

        sendAndRecordReminder(event, recipients, content);
    }

    private void notifyEventUpdated(final Event event) {
        String content = "이벤트 정보가 수정되었습니다.";
        List<OrganizationMember> recipients = event.getGuests()
                .stream()
                .map(Guest::getOrganizationMember)
                .toList();

        sendAndRecordReminder(event, recipients, content);
    }

    private void sendAndRecordReminder(
            final Event event,
            final List<OrganizationMember> recipients,
            final String content
    ) {
        List<ReminderHistory> reminderHistories = reminder.remind(recipients, event, content);
        reminderHistoryRepository.saveAll(reminderHistories);
    }
}
