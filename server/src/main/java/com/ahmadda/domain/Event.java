package com.ahmadda.domain;


import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.domain.util.Assert;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

    private static final int MIN_CAPACITY = 1;
    private static final int MAX_CAPACITY = 2_100_000_000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    private String place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private OrganizationMember organizer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event")
    private List<Guest> guests = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @Embedded
    private EventOperationPeriod eventOperationPeriod;

    @Column(nullable = false)
    private String organizerNickname;

    @Column(nullable = false)
    private int maxCapacity;

    private Event(
            final String title,
            final String description,
            final String place,
            final OrganizationMember organizer,
            final Organization organization,
            final EventOperationPeriod eventOperationPeriod,
            final String organizerNickname,
            final int maxCapacity,
            final List<Question> questions
    ) {
        validateTitle(title);
        validateOrganizer(organizer);
        validateOrganization(organization);
        validateBelongToOrganization(organizer, organization);
        validateOrganizerNickname(organizerNickname);
        validateMaxCapacity(maxCapacity);

        this.title = title;
        this.description = description;
        this.place = place;
        this.organizer = organizer;
        this.organization = organization;
        this.eventOperationPeriod = eventOperationPeriod;
        this.organizerNickname = organizerNickname;
        this.maxCapacity = maxCapacity;

        organization.addEvent(this);
        this.questions.addAll(questions);
    }

    public static Event create(
            final String title,
            final String description,
            final String place,
            final OrganizationMember organizer,
            final Organization organization,
            final EventOperationPeriod eventOperationPeriod,
            final String organizerNickname,
            final int maxCapacity,
            final Question... questions
    ) {
        return new Event(
                title,
                description,
                place,
                organizer,
                organization,
                eventOperationPeriod,
                organizerNickname,
                maxCapacity,
                new ArrayList<>(List.of(questions))
        );
    }

    public static Event create(
            final String title,
            final String description,
            final String place,
            final OrganizationMember organizer,
            final Organization organization,
            final EventOperationPeriod eventOperationPeriod,
            final String organizerNickname,
            final int maxCapacity,
            final List<Question> questions
    ) {
        return new Event(
                title,
                description,
                place,
                organizer,
                organization,
                eventOperationPeriod,
                organizerNickname,
                maxCapacity,
                questions
        );
    }

    public boolean hasGuest(final OrganizationMember organizationMember) {
        if (organizationMember.equals(organizer)) {
            return true;
        }

        return guests.stream()
                .anyMatch(guest -> guest.isSameOrganizationMember(organizationMember));
    }

    public List<OrganizationMember> getNonGuestOrganizationMembers(final List<OrganizationMember> organizationMembers) {
        Set<OrganizationMember> participants = guests.stream()
                .map(Guest::getOrganizationMember)
                .collect(Collectors.toSet());
        participants.add(organizer);

        return organizationMembers.stream()
                .filter(organizationMember -> !participants.contains(organizationMember))
                .toList();
    }

    public boolean isNotStarted(final LocalDateTime currentDateTime) {
        return eventOperationPeriod.isNotStarted(currentDateTime);
    }

    public void participate(final Guest guest, final LocalDateTime participantDateTime) {
        validateParticipate(guest, participantDateTime);

        this.guests.add(guest);
    }

    public boolean hasQuestion(final Question question) {
        return questions.contains(question);
    }

    public Set<Question> getRequiredQuestions() {
        return getQuestions()
                .stream()
                .filter(Question::isRequired)
                .collect(Collectors.toSet());
    }

    public void closeRegistrationAt(final OrganizationMember organizationMember,
                                    final LocalDateTime registrationEndTime) {
        validateCloseRegistration(organizationMember.getMember());

        this.eventOperationPeriod.closeRegistration(registrationEndTime);
    }

    public boolean isOrganizer(final Member member) {
        return organizer.getMember()
                .equals(member);
    }

    private void validateParticipate(final Guest guest, final LocalDateTime participantDateTime) {
        if (eventOperationPeriod.canNotRegistration(participantDateTime)) {
            throw new BusinessRuleViolatedException("이벤트 신청은 신청 시작 시간부터 신청 마감 시간까지 가능합니다.");
        }
        if (guests.size() >= maxCapacity) {
            throw new BusinessRuleViolatedException("수용 인원이 가득차 이벤트에 참여할 수 없습니다.");
        }
        if (guest.isSameOrganizationMember(organizer)) {
            throw new BusinessRuleViolatedException("이벤트의 주최자는 게스트로 참여할 수 없습니다.");
        }
        if (hasGuest(guest.getOrganizationMember())) {
            throw new BusinessRuleViolatedException("이미 해당 이벤트에 참여중인 게스트입니다.");
        }
    }

    private void validateCloseRegistration(Member organizer) {
        if (!isOrganizer(organizer)) {
            throw new AccessDeniedException("주최자만 마감할 수 있습니다.");
        }
    }

    private void validateTitle(final String title) {
        Assert.notBlank(title, "제목은 공백이면 안됩니다.");
    }

    private void validateOrganizer(final OrganizationMember organizer) {
        Assert.notNull(organizer, "주최자는 null이 되면 안됩니다.");
    }

    private void validateOrganization(final Organization organization) {
        Assert.notNull(organization, "조직은 null이 되면 안됩니다.");
    }

    private void validateBelongToOrganization(final OrganizationMember organizer, final Organization organization) {
        if (!organizer.isBelongTo(organization)) {
            throw new BusinessRuleViolatedException("자신이 속한 조직에서만 이벤트를 생성할 수 있습니다.");
        }
    }

    private void validateOrganizerNickname(final String organizerNickname) {
        Assert.notBlank(organizerNickname, "주최자 이름은 공백이면 안됩니다.");
    }

    private void validateMaxCapacity(final int maxCapacity) {
        if (maxCapacity < MIN_CAPACITY || maxCapacity > MAX_CAPACITY) {
            throw new BusinessRuleViolatedException("최대 수용 인원은 1명보다 적거나 21억명 보다 클 수 없습니다.");
        }
    }

    public LocalDateTime getRegistrationStart() {
        return eventOperationPeriod.getRegistrationPeriod()
                .start();
    }

    public LocalDateTime getRegistrationEnd() {
        return eventOperationPeriod.getRegistrationPeriod()
                .end();
    }

    public LocalDateTime getEventStart() {
        return eventOperationPeriod.getEventPeriod()
                .start();
    }

    public LocalDateTime getEventEnd() {
        return eventOperationPeriod.getEventPeriod()
                .end();
    }
}
