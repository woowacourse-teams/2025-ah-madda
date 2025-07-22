package com.ahmadda.application;

import com.ahmadda.application.dto.AnswerCreateRequest;
import com.ahmadda.application.dto.EventParticipateRequest;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Guest;
import com.ahmadda.domain.GuestRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.Question;
import com.ahmadda.domain.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventGuestService {

    private final GuestRepository guestRepository;
    private final EventRepository eventRepository;
    private final QuestionRepository questionRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    // TODO. 추후 주최자에 대한 인가 처리 필요
    public List<Guest> getGuests(final Long eventId) {
        final Event event = getEvent(eventId);

        return event.getGuests();
    }

    // TODO. 추후 주최자에 대한 인가 처리 필요
    public List<OrganizationMember> getNonGuestOrganizationMembers(final Long eventId) {
        final Event event = getEvent(eventId);
        final Organization organization = event.getOrganization();
        final List<OrganizationMember> allMembers = organization.getOrganizationMembers();

        return event.getNonGuestOrganizationMembers(allMembers);
    }

    @Transactional
    public void participantEvent(
            final Long eventId,
            final Long organizationMemberId,
            final LocalDateTime currentDateTime,
            final EventParticipateRequest eventParticipateRequest
    ) {
        Event event = getEvent(eventId);
        OrganizationMember organizationMember = getOrganizationMember(organizationMemberId);

        Guest guest = Guest.create(event, organizationMember, currentDateTime);

        Map<Question, String> questionAnswers = getQuestionAnswers(eventParticipateRequest.answers());
        guest.submitAnswers(questionAnswers);

        guestRepository.save(guest);
    }

    private Event getEvent(final Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다."));
    }

    private OrganizationMember getOrganizationMember(final Long organizationMemberId) {
        return organizationMemberRepository.findById(organizationMemberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직원입니다."));
    }

    private Map<Question, String> getQuestionAnswers(final List<AnswerCreateRequest> answerCreateRequests) {
        return answerCreateRequests
                .stream()
                .map(answerRequest -> Map.entry(getQuestion(answerRequest.questionId()), answerRequest.answerText()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Question getQuestion(final Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 질문입니다."));
    }
}
