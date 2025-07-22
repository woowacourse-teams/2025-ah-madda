package com.ahmadda.application;

import com.ahmadda.application.dto.EventCreateRequest;
import com.ahmadda.application.dto.QuestionCreateRequest;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
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

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Transactional
    public Event createEvent(
            final Long organizationId,
            final Long organizerId,
            final EventCreateRequest eventCreateRequest
    ) {
        Organization organization = getOrganization(organizationId);
        OrganizationMember organizer = getOrganizationMember(organizerId);

        EventOperationPeriod eventOperationPeriod = createEventOperationPeriod(eventCreateRequest);
        Event event = Event.create(
                eventCreateRequest.title(),
                eventCreateRequest.description(),
                eventCreateRequest.place(),
                organizer,
                organization,
                eventOperationPeriod,
                eventCreateRequest.maxCapacity()
        );
        addQuestionsToEvent(event, eventCreateRequest.questions());

        return eventRepository.save(event);
    }

    public Event getEvent(final Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않은 이벤트 정보입니다."));
    }

    private EventOperationPeriod createEventOperationPeriod(final EventCreateRequest eventCreateRequest) {
        Period registrationPeriod =
                Period.create(eventCreateRequest.registrationStart(), eventCreateRequest.registrationEnd());
        Period eventPeriod = Period.create(eventCreateRequest.eventStart(), eventCreateRequest.eventEnd());

        return EventOperationPeriod.create(
                registrationPeriod,
                eventPeriod,
                LocalDateTime.now()
        );
    }

    private Organization getOrganization(final Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않은 조직 정보입니다."));
    }

    private OrganizationMember getOrganizationMember(final Long organizationMemberId) {
        return organizationMemberRepository.findById(organizationMemberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않은 조직원 정보입니다."));
    }

    private void addQuestionsToEvent(
            final Event event,
            final List<QuestionCreateRequest> questionCreateRequests
    ) {
        for (int i = 0; i < questionCreateRequests.size(); i++) {
            QuestionCreateRequest request = questionCreateRequests.get(i);
            Question question = Question.create(event, request.questionText(), request.isRequired(), i);
            event.addQuestions(question);
        }
    }
}
