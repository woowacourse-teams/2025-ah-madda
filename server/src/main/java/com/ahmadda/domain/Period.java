package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.domain.util.Assert;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Period {

    private LocalDateTime start;
    private LocalDateTime end;

    private Period(final LocalDateTime start, final LocalDateTime end) {
        Assert.notNull(start, "시작 시간은 null일 수 없습니다.");
        Assert.notNull(end, "종료 시간은 null일 수 없습니다.");

        if (end.equals(start) || end.isBefore(start)) {
            throw new BusinessRuleViolatedException("종료 시간은 시작 시간보다 미래여야 합니다.");
        }

        this.start = start;
        this.end = end;
    }

    public static Period create(final LocalDateTime start, final LocalDateTime end) {
        return new Period(start, end);
    }

    public boolean isAfter(final Period other) {
        return this.start.isAfter(other.end);
    }

    public boolean isOverlappedWith(final Period other) {
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
