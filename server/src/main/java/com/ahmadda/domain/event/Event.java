package com.ahmadda.domain.event;


import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.BaseEntity;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE event SET deleted_at = CURRENT_TIMESTAMP WHERE event_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Event extends BaseEntity {

    private static final int MIN_CAPACITY = 1;
    private static final int MAX_CAPACITY = 2_100_000_000;
    private static final Duration BEFORE_EVENT_STARTED_CANCEL_AVAILABLE_MINUTE = Duration.ofMinutes(10);
    private static final int MAX_EVENT_ORGANIZERS_CAPACITY = 10;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event")
    private final List<Guest> guests = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "event_id", nullable = false)
    private final List<Question> questions = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<EventOrganizer> eventOrganizers = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Nullable
    private String description;

    @Nullable
    private String place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Embedded
    private EventOperationPeriod eventOperationPeriod;

    @Column(nullable = false)
    private int maxCapacity;

    private Event(
            final String title,
            final String description,
            final String place,
            final Organization organization,
            final EventOperationPeriod eventOperationPeriod,
            final int maxCapacity,
            final List<OrganizationMember> eventOrganizers,
            final List<Question> questions
    ) {
        validateMaxCapacity(maxCapacity);
        validateEventOrganizersMaxCapacity(eventOrganizers);

        this.title = title;
        this.description = description;
        this.place = place;
        this.organization = organization;
        this.eventOperationPeriod = eventOperationPeriod;
        this.maxCapacity = maxCapacity;

        organization.addEvent(this);
        this.questions.addAll(questions);
        this.eventOrganizers.addAll(createEventOrganizers(eventOrganizers));
    }

    public static Event create(
            final String title,
            final String description,
            final String place,
            final Organization organization,
            final EventOperationPeriod eventOperationPeriod,
            final List<OrganizationMember> eventOrganizers,
            final int maxCapacity,
            final Question... questions
    ) {
        return new Event(
                title,
                description,
                place,
                organization,
                eventOperationPeriod,
                maxCapacity,
                eventOrganizers,
                new ArrayList<>(List.of(questions))
        );
    }

    public static Event create(
            final String title,
            final String description,
            final String place,
            final Organization organization,
            final EventOperationPeriod eventOperationPeriod,
            final int maxCapacity,
            final Question... questions
    ) {
        return new Event(
                title,
                description,
                place,
                organization,
                eventOperationPeriod,
                maxCapacity,
                List.of(),
                new ArrayList<>(List.of(questions))
        );
    }

    public static Event create(
            final String title,
            final String description,
            final String place,
            final Organization organization,
            final EventOperationPeriod eventOperationPeriod,
            final int maxCapacity,
            final List<OrganizationMember> eventOrganizers,
            final List<Question> questions
    ) {
        return new Event(
                title,
                description,
                place,
                organization,
                eventOperationPeriod,
                maxCapacity,
                eventOrganizers,
                questions
        );
    }

    public static Event create(
            final String title,
            final String description,
            final String place,
            final Organization organization,
            final EventOperationPeriod eventOperationPeriod,
            final int maxCapacity,
            final List<Question> questions
    ) {
        return new Event(
                title,
                description,
                place,
                organization,
                eventOperationPeriod,
                maxCapacity,
                List.of(),
                questions
        );
    }

    public static Event create(
            String title,
            String description,
            String place,
            OrganizationMember organizer,
            Organization organization,
            EventOperationPeriod eventOperationPeriod,
            int maxCapacity
    ) {
        return new Event(
                title,
                description,
                place,
                organization,
                eventOperationPeriod,
                maxCapacity,
                List.of(organizer),
                List.of()
        );
    }

    public static Event create(
            String title,
            String description,
            String place,
            OrganizationMember organizer,
            Organization organization,
            EventOperationPeriod eventOperationPeriod,
            int maxCapacity,
            List<Question> questions
    ) {
        return new Event(
                title,
                description,
                place,
                organization,
                eventOperationPeriod,
                maxCapacity,
                List.of(organizer),
                questions
        );
    }

    public static Event create(
            String title,
            String description,
            String place,
            OrganizationMember organizer,
            Organization organization,
            EventOperationPeriod eventOperationPeriod,
            int maxCapacity,
            Question... questions
    ) {
        return new Event(
                title,
                description,
                place,
                organization,
                eventOperationPeriod,
                maxCapacity,
                List.of(organizer),
                new ArrayList<>(List.of(questions))
        );
    }

    public void update(
            final Member organizer,
            final String title,
            final String description,
            final String place,
            final EventOperationPeriod eventOperationPeriod,
            final int maxCapacity
    ) {
        validateUpdatableBy(organizer);
        validateMaxCapacity(maxCapacity);

        this.title = title;
        this.description = description;
        this.place = place;
        this.eventOperationPeriod = eventOperationPeriod;
        this.maxCapacity = maxCapacity;
    }

    public boolean hasGuest(final OrganizationMember organizationMember) {
        return guests.stream()
                .anyMatch(guest -> guest.isSameOrganizationMember(organizationMember));
    }

    public List<OrganizationMember> getNonGuestOrganizationMembers(final List<OrganizationMember> allOrganizationMembers) {
        Set<OrganizationMember> participants = guests.stream()
                .map(Guest::getOrganizationMember)
                .collect(Collectors.toSet());

        List<OrganizationMember> eventOrganizerList = eventOrganizers.stream()
                .map(EventOrganizer::getOrganizationMember)
                .toList();

        participants.addAll(eventOrganizerList);

        return allOrganizationMembers.stream()
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

    public void closeRegistrationAt(
            final OrganizationMember organizationMember,
            final LocalDateTime registrationEndTime
    ) {
        validateClosableBy(organizationMember.getMember());

        this.eventOperationPeriod.closeRegistration(registrationEndTime);
    }

    public boolean isOrganizer(final Member member) {
        return eventOrganizers.stream()
                .anyMatch((eventOrganizer) -> eventOrganizer.isSameMember(member));
    }

    public boolean isOrganizer(final OrganizationMember organizationMember) {
        return eventOrganizers.stream()
                .anyMatch((eventOrganizer) -> eventOrganizer.isSameOrganizationMember(
                        organizationMember));
    }

    public void cancelParticipation(
            final OrganizationMember organizationMember,
            final LocalDateTime cancelParticipateTime
    ) {
        validateCancelParticipation(cancelParticipateTime);
        Guest guest = getGuestByOrganizationMember(organizationMember);
        guests.remove(guest);
    }

    public boolean isRegistrationEnd(final LocalDateTime currentDateTime) {
        return eventOperationPeriod.getRegistrationEventPeriod()
                .end()
                .isAfter(currentDateTime);
    }

    public boolean isFull() {
        return guests.size() >= maxCapacity;
    }

    private void validateEventOrganizersMaxCapacity(final List<OrganizationMember> eventOrganizers) {
        if (eventOrganizers.size() > MAX_EVENT_ORGANIZERS_CAPACITY) {
            throw new UnprocessableEntityException("최대 주최자 수는 10명입니다.");
        }
    }

    private List<EventOrganizer> createEventOrganizers(final List<OrganizationMember> eventOrganizers) {
        validateDuplicateEventOrganizers(eventOrganizers);

        Set<OrganizationMember> organizationMembers = new HashSet<>(eventOrganizers);

        return organizationMembers.stream()
                .map(organizationMember -> new EventOrganizer(this, organizationMember))
                .toList();
    }

    private void validateDuplicateEventOrganizers(
            final List<OrganizationMember> eventOrganizers
    ) {
        Set<OrganizationMember> distinctOrganizationMembers = new HashSet<>(eventOrganizers);
        List<OrganizationMember> organizerIncludeOrganizationMembers = new ArrayList<>(eventOrganizers);

        if (organizerIncludeOrganizationMembers.size() != distinctOrganizationMembers.size()) {
            throw new UnprocessableEntityException("주최자는 중복될 수 없습니다.");
        }
    }

    private void validateCancelParticipation(final LocalDateTime cancelParticipationTime) {
        if (eventOperationPeriod.willStartWithin(
                cancelParticipationTime,
                BEFORE_EVENT_STARTED_CANCEL_AVAILABLE_MINUTE
        )) {
            throw new UnprocessableEntityException("이벤트 시작전 10분 이후로는 신청을 취소할 수 없습니다");
        }
    }

    private void validateParticipate(final Guest guest, final LocalDateTime participantDateTime) {
        if (eventOperationPeriod.canNotRegistration(participantDateTime)) {
            throw new UnprocessableEntityException("이벤트 신청은 신청 시작 시간부터 신청 마감 시간까지 가능합니다.");
        }
        if (guests.size() >= maxCapacity) {
            throw new UnprocessableEntityException("수용 인원이 가득차 이벤트에 참여할 수 없습니다.");
        }

        if (isOrganizer(guest.getOrganizationMember())) {
            throw new UnprocessableEntityException("이벤트의 주최자는 게스트로 참여할 수 없습니다.");
        }
        if (hasGuest(guest.getOrganizationMember())) {
            throw new UnprocessableEntityException("이미 해당 이벤트에 참여 중인 게스트입니다.");
        }
    }

    private void validateUpdatableBy(final Member organizer) {
        if (!isOrganizer(organizer)) {
            throw new ForbiddenException("이벤트의 주최자만 수정할 수 있습니다.");
        }
    }

    private void validateClosableBy(final Member organizer) {
        if (!isOrganizer(organizer)) {
            throw new ForbiddenException("이벤트의 주최자만 마감할 수 있습니다.");
        }
    }

    private void validateMaxCapacity(final int maxCapacity) {
        if (maxCapacity < MIN_CAPACITY || maxCapacity > MAX_CAPACITY) {
            throw new UnprocessableEntityException("최대 수용 인원은 1명보다 적거나 21억명 보다 클 수 없습니다.");
        }
    }


    private Guest getGuestByOrganizationMember(final OrganizationMember organizationMember) {
        return guests.stream()
                .filter((guest) -> guest.isSameOrganizationMember(organizationMember))
                .findAny()
                .orElseThrow(() -> new UnprocessableEntityException("이벤트의 참가자 목록에서 일치하는 구성원을 찾을 수 없습니다"));
    }

    public LocalDateTime getRegistrationStart() {
        return eventOperationPeriod.getRegistrationEventPeriod()
                .start();
    }

    public LocalDateTime getRegistrationEnd() {
        return eventOperationPeriod.getRegistrationEventPeriod()
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

    public boolean isBeforeEventEnd(final LocalDateTime currentDateTime) {
        return eventOperationPeriod.isBeforeEventEnd(currentDateTime);
    }
}
