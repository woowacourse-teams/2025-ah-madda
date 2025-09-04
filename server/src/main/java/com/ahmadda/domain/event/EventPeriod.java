package com.ahmadda.domain.event;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventPeriod {

    private LocalDateTime start;
    private LocalDateTime end;

    private EventPeriod(final LocalDateTime start, final LocalDateTime end) {
        if (end.equals(start) || end.isBefore(start)) {
            throw new BusinessRuleViolatedException("종료 시간은 시작 시간보다 미래여야 합니다.");
        }

        this.start = start;
        this.end = end;
    }

    public static EventPeriod create(final LocalDateTime start, final LocalDateTime end) {
        return new EventPeriod(start, end);
    }

    public boolean isAfter(final EventPeriod other) {
        return this.start.isAfter(other.end);
    }

    public boolean isOverlappedWith(final EventPeriod other) {
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }

    public boolean isNotStarted(final LocalDateTime currentDateTime) {
        return start.isAfter(currentDateTime);
    }

    public boolean isBeforeEnd(final LocalDateTime currentDateTime) {
        return end.isAfter(currentDateTime);
    }

    public boolean includes(final LocalDateTime dateTime) {
        return (start.isEqual(dateTime) || start.isBefore(dateTime)) &&
                (end.isEqual(dateTime) || end.isAfter(dateTime));
    }

    public LocalDateTime start() {
        return start;
    }

    public LocalDateTime end() {
        return end;
    }
}
