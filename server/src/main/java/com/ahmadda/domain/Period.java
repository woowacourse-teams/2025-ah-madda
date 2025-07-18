package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.domain.util.Assert;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public record Period(
        LocalDateTime start,
        LocalDateTime end
) {

    public Period {
        Assert.notNull(start, "시작 시간은 null일 수 없습니다.");
        Assert.notNull(end, "종료 시간은 null일 수 없습니다.");

        if (end.equals(start) || end.isBefore(start)) {
            throw new BusinessRuleViolatedException("종료 시간은 시작 시간보다 미래여야 합니다.");
        }
    }

    public boolean isAfter(final Period other) {
        return this.start.isAfter(other.end);
    }

    public boolean isBefore(final Period other) {
        return this.end.isBefore(other.start);
    }

    public boolean overlaps(final Period other) {
        return !this.isAfter(other) && !this.isBefore(other);
    }
}
