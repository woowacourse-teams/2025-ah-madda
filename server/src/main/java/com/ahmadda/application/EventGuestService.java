package com.ahmadda.application;

import com.ahmadda.application.dto.AnswerCreateRequest;
import com.ahmadda.application.dto.EventParticipateRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Answer;
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

    public List<Guest> getGuests(final Long eventId, final LoginMember loginMember) {
        Event event = getEvent(eventId);
        Organization organization = event.getOrganization();
        validateOrganizationAccess(loginMember, organization);

        return event.getGuests();
    }

    public List<OrganizationMember> getNonGuestOrganizationMembers(final Long eventId, final LoginMember loginMember) {
        Event event = getEvent(eventId);
        Organization organization = event.getOrganization();
        validateOrganizationAccess(loginMember, organization);

        List<OrganizationMember> allMembers = organization.getOrganizationMembers();

        return event.getNonGuestOrganizationMembers(allMembers);
    }

    @Transactional
    public void participantEvent(
            final Long eventId,
            final LoginMember loginMember,
            final LocalDateTime currentDateTime,
            final EventParticipateRequest eventParticipateRequest
    ) {
        Event event = getEvent(eventId);
        Organization organization = event.getOrganization();
        OrganizationMember organizationMember = getOrganizationMember(organization.getId(), loginMember.memberId());

        Guest guest = Guest.create(event, organizationMember, currentDateTime);

        Map<Question, String> questionAnswers = getQuestionAnswers(eventParticipateRequest.answers());
        guest.submitAnswers(questionAnswers);

        guestRepository.save(guest);
    }

    @Transactional
    public void cancelParticipation(
            final Long eventId,
            final LoginMember loginMember
    ) {
        Event event = getEvent(eventId);
        Long organizationId = event.getOrganization()
                .getId();
        OrganizationMember organizationMember = getOrganizationMember(organizationId, loginMember.memberId());

        event.cancelParticipation(organizationMember, LocalDateTime.now());
        guestRepository.deleteByEventAndOrganizationMember(event, organizationMember);
    }

    public boolean isGuest(final Long eventId, final LoginMember loginMember) {
        Event event = getEvent(eventId);
        Organization organization = event.getOrganization();
        OrganizationMember organizationMember = getOrganizationMember(organization.getId(), loginMember.memberId());

        return event.hasGuest(organizationMember);
    }

    public List<Answer> getAnswers(final Long eventId, Long guestId, final LoginMember organizerLoginMember) {
        Event event = getEvent(eventId);
        Organization organization = event.getOrganization();
        OrganizationMember organizer = getOrganizationMember(organization.getId(), organizerLoginMember.memberId());
        Guest guest = getGuest(guestId);

        return guest.viewAnswersAs(organizer);
    }

    private void validateOrganizationAccess(final LoginMember loginMember, final Organization organization) {
        if (!organizationMemberRepository.existsByOrganizationIdAndMemberId(
                organization.getId(),
                loginMember.memberId()
        )) {
            throw new AccessDeniedException("조직의 조직원만 접근할 수 있습니다.");
        }
    }

    private Event getEvent(final Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다."));
    }

    private OrganizationMember getOrganizationMember(final Long organizationId, final Long memberId) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, memberId)
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

    private Guest getGuest(Long guestId) {
        return guestRepository.findById(guestId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게스트입니다."));
    }
}
