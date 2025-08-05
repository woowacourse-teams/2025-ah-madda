package com.ahmadda.application;

import com.ahmadda.application.dto.EventCreateRequest;
import com.ahmadda.application.dto.EventUpdateRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.QuestionCreateRequest;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventEmailPayload;
import com.ahmadda.domain.EventNotification;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.EventStatistic;
import com.ahmadda.domain.EventStatisticRepository;
import com.ahmadda.domain.Guest;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.domain.Question;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final EventNotification eventNotification;
    private final EventStatisticRepository eventStatisticRepository;

    @Transactional
    public Event createEvent(
            final Long organizationId,
            final LoginMember loginMember,
            final EventCreateRequest eventCreateRequest,
            final LocalDateTime currentDateTime
    ) {
        Organization organization = getOrganization(organizationId);
        OrganizationMember organizer = validateOrganizationAccess(organizationId, loginMember.memberId());

        EventOperationPeriod eventOperationPeriod = createEventOperationPeriod(eventCreateRequest, currentDateTime);
        Event event = Event.create(
                eventCreateRequest.title(),
                eventCreateRequest.description(),
                eventCreateRequest.place(),
                organizer,
                organization,
                eventOperationPeriod,
                eventCreateRequest.organizerNickname(),
                eventCreateRequest.maxCapacity(),
                createQuestions(eventCreateRequest.questions())
        );

        Event savedEvent = eventRepository.save(event);
        notifyEventCreated(savedEvent, organization);
        createEventStatistic(savedEvent);

        return savedEvent;
    }

    private void createEventStatistic(final Event savedEvent) {
        eventStatisticRepository.save(EventStatistic.create(savedEvent));
    }

    @Transactional
    public void closeEventRegistration(
            final Long eventId,
            final Long memberId,
            final LocalDateTime currentDateTime
    ) {
        Event event = getEvent(eventId);
        Organization organization = event.getOrganization();

        OrganizationMember organizationMember = validateOrganizationAccess(organization.getId(), memberId);

        event.closeRegistrationAt(organizationMember, currentDateTime);
    }

    //TODO 추후에 EventListener에 대해 협의해본뒤 리팩터링
    @Transactional
    public Event getOrganizationMemberEvent(final LoginMember loginMember, final Long eventId) {
        Event event = getEvent(eventId);

        Organization organization = event.getOrganization();
        validateOrganizationAccess(organization.getId(), loginMember.memberId());

        //TODO 추후에 EventListener에 대해 협의해본뒤 리팩터링
        try {
            EventStatistic eventStatistic = eventStatisticRepository.findByEventId(eventId)
                    .orElseThrow(() -> new NotFoundException("해당되는 이벤트 조회수를 가져오는데 실패하였습니다."));
            eventStatistic.increaseViewCount(LocalDate.now());
        } catch (Exception e) {
            log.error("이벤트 조회수를 업데이트하는데 실패하였습니다 사유 : {}", e.getMessage(), e);
        }

        return event;
    }

    @Transactional
    public Event updateEvent(
            final Long eventId,
            final LoginMember loginMember,
            final EventUpdateRequest eventUpdateRequest,
            final LocalDateTime currentDateTime
    ) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않은 이벤트 정보입니다."));
        Member member = memberRepository.findById(loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

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
                eventUpdateRequest.organizerNickname(),
                eventUpdateRequest.maxCapacity()
        );

        notifyEventUpdated(event);

        return event;
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

    private OrganizationMember validateOrganizationAccess(final Long organizationId, final Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, memberId)
                .orElseThrow(() -> new AccessDeniedException("조직에 소속되지 않은 회원입니다."));
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
        List<OrganizationMember> recipients =
                event.getNonGuestOrganizationMembers(organization.getOrganizationMembers());
        String content = "새로운 이벤트가 등록되었습니다.";
        EventEmailPayload eventEmailPayload = EventEmailPayload.of(event, content);

        eventNotification.sendEmails(recipients, eventEmailPayload);
    }

    private void notifyEventUpdated(final Event event) {
        List<OrganizationMember> recipients = event.getGuests()
                .stream()
                .map(Guest::getOrganizationMember)
                .toList();
        String content = "이벤트 정보가 수정되었습니다.";
        EventEmailPayload eventEmailPayload = EventEmailPayload.of(event, content);

        eventNotification.sendEmails(recipients, eventEmailPayload);
    }
}
