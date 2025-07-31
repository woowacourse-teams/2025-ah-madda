package com.ahmadda.application;

import com.ahmadda.application.dto.EventCreateRequest;
import com.ahmadda.application.dto.EventUpdateRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.QuestionCreateRequest;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Email;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventNotification;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.domain.Period;
import com.ahmadda.domain.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class EventService {

    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final EventNotification eventNotification;

    @Transactional
    public Event createEvent(
            final Long organizationId,
            final Long memberId,
            final EventCreateRequest eventCreateRequest,
            final LocalDateTime currentDateTime
    ) {
        Organization organization = getOrganization(organizationId);
        OrganizationMember organizer = validateOrganizationAccess(organizationId, memberId);

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

        return savedEvent;
    }

    public Event getEvent(final Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않은 이벤트 정보입니다."));
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
        if (!event.isOrganizer(member)) {
            throw new AccessDeniedException("이벤트의 주최자만 수정할 수 있습니다.");
        }

        Period updatedRegistrationPeriod = event.getEventOperationPeriod()
                .getRegistrationPeriod()
                .update(
                        event.getEventOperationPeriod()
                                .getRegistrationPeriod()
                                .start(),
                        eventUpdateRequest.registrationEnd()
                );
        Period updatedEventPeriod = event.getEventOperationPeriod()
                .getEventPeriod()
                .update(eventUpdateRequest.eventStart(), eventUpdateRequest.eventEnd());

        EventOperationPeriod updatedOperationPeriod = event.getEventOperationPeriod()
                .update(updatedRegistrationPeriod, updatedEventPeriod, currentDateTime);

        event.update(
                eventUpdateRequest.title(),
                eventUpdateRequest.description(),
                eventUpdateRequest.place(),
                updatedOperationPeriod,
                eventUpdateRequest.organizerNickname(),
                eventUpdateRequest.maxCapacity()
        );

        // notifyEventUpdated(event);

        return event;
    }

    private EventOperationPeriod createEventOperationPeriod(
            final EventCreateRequest eventCreateRequest,
            final LocalDateTime currentDateTime
    ) {
        Period registrationPeriod = Period.create(currentDateTime, eventCreateRequest.registrationEnd());
        Period eventPeriod = Period.create(eventCreateRequest.eventStart(), eventCreateRequest.eventEnd());

        return EventOperationPeriod.create(
                registrationPeriod,
                eventPeriod,
                currentDateTime
        );
    }

    private Organization getOrganization(final Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않은 조직 정보입니다."));
    }

    private OrganizationMember validateOrganizationAccess(final Long organizationId, final Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, memberId)
                .orElseThrow(() -> new AccessDeniedException("조직에 소속되지 않은 멤버입니다."));
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
        Email email = Email.of(event, content);

        eventNotification.sendEmails(recipients, email);
    }
}
