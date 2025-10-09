package com.ahmadda.domain.event;

import com.ahmadda.common.exception.UnprocessableEntityException;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventOperationPeriod {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "registration_start")),
            @AttributeOverride(name = "end", column = @Column(name = "registration_end"))
    })
    private EventPeriod registrationEventPeriod;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "event_start")),
            @AttributeOverride(name = "end", column = @Column(name = "event_end"))
    })
    private EventPeriod eventPeriod;

    private EventOperationPeriod(
            final EventPeriod registrationEventPeriod,
            final EventPeriod eventPeriod,
            final LocalDateTime currentDateTime
    ) {
        validateEventPeriod(eventPeriod, currentDateTime);
        validatePeriodRelationship(registrationEventPeriod, eventPeriod);

        this.registrationEventPeriod = registrationEventPeriod;
        this.eventPeriod = eventPeriod;
    }

    public static EventOperationPeriod create(
            final LocalDateTime registrationStart,
            final LocalDateTime registrationEnd,
            final LocalDateTime eventStart,
            final LocalDateTime eventEnd,
            final LocalDateTime currentDateTime
    ) {
        EventPeriod registrationEventPeriod = EventPeriod.create(registrationStart, registrationEnd);
        EventPeriod eventPeriod = EventPeriod.create(eventStart, eventEnd);

        return new EventOperationPeriod(registrationEventPeriod, eventPeriod, currentDateTime);
    }

    public boolean isNotStarted(final LocalDateTime currentDateTime) {
        return eventPeriod.isNotStarted(currentDateTime);
    }

    public boolean isAfterEventEndDate(final LocalDate currentDate) {
        LocalDate endDate = LocalDate.from(eventPeriod.end());
        return endDate.isBefore(currentDate);
    }

    public boolean canNotRegistration(final LocalDateTime currentDateTime) {
        return !registrationEventPeriod.includes(currentDateTime);
    }

    public void closeRegistration(final LocalDateTime closeTime) {
        EventPeriod closeEventPeriod = EventPeriod.create(this.registrationEventPeriod.start(), closeTime);

        validateClosePeriod(closeTime);
        validatePeriodRelationship(closeEventPeriod, this.eventPeriod);

        this.registrationEventPeriod = closeEventPeriod;
    }

    private void validateClosePeriod(final LocalDateTime closeDateTime) {
        if (closeDateTime.isAfter(this.registrationEventPeriod.end())) {
            throw new UnprocessableEntityException("이미 신청이 마감된 이벤트입니다.");
        }
    }

    private void validateEventPeriod(final EventPeriod eventPeriod, final LocalDateTime currentDateTime) {
        if (eventPeriod.start()
                .isBefore(currentDateTime)) {
            throw new UnprocessableEntityException("이벤트 시작 시간은 현재 시점보다 미래여야 합니다.");
        }
    }

    private void validatePeriodRelationship(final EventPeriod registrationEventPeriod, final EventPeriod eventPeriod) {
        if (registrationEventPeriod.isOverlappedWith(eventPeriod)) {
            throw new UnprocessableEntityException("신청 기간과 이벤트 기간이 겹칠 수 없습니다.");
        }
        if (registrationEventPeriod.isAfter(eventPeriod)) {
            throw new UnprocessableEntityException("신청 기간은 이벤트 기간보다 앞서야 합니다.");
        }
    }

    public boolean willStartWithin(final LocalDateTime cancelParticipationTime, final Duration duration) {
        LocalDateTime cancelAvailableTime = eventPeriod.start()
                .minus(duration);

        return cancelParticipationTime.isAfter(cancelAvailableTime);
    }

    public boolean isBeforeEventEnd(final LocalDateTime currentDateTime) {
        return eventPeriod.isBeforeEnd(currentDateTime);
    }
}
