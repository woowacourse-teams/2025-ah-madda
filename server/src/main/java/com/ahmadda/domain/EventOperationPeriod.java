package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.domain.util.Assert;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Period registrationPeriod;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "event_start")),
            @AttributeOverride(name = "end", column = @Column(name = "event_end"))
    })
    private Period eventPeriod;

    private EventOperationPeriod(Period registrationPeriod, Period eventPeriod) {
        validate(registrationPeriod, eventPeriod);

        this.registrationPeriod = registrationPeriod;
        this.eventPeriod = eventPeriod;
    }

    public static EventOperationPeriod create(
            final Period registrationPeriod,
            final Period eventPeriod,
            final LocalDateTime currentDateTime
    ) {
        validateRegistrationPeriod(registrationPeriod, currentDateTime);
        validateEventPeriod(eventPeriod, currentDateTime);

        return new EventOperationPeriod(registrationPeriod, eventPeriod);
    }

    private static void validateRegistrationPeriod(Period registrationPeriod, LocalDateTime currentDateTime) {
        Assert.notNull(registrationPeriod, "이벤트 신청 기간은 null이 되면 안됩니다.");

        if (registrationPeriod.start().isBefore(currentDateTime)) {
            throw new BusinessRuleViolatedException("이벤트 신청 시작 시간은 현재 시점보다 미래여야 합니다.");
        }
    }

    private static void validateEventPeriod(Period eventPeriod, LocalDateTime currentDateTime) {
        Assert.notNull(eventPeriod, "이벤트 신청 기간은 null이 되면 안됩니다.");

        if (eventPeriod.start().isBefore(currentDateTime)) {
            throw new BusinessRuleViolatedException("이벤트 시작 시간은 현재 시점보다 미래여야 합니다.");
        }
    }

    private void validate(Period registrationPeriod, Period eventPeriod) {
        Assert.notNull(registrationPeriod, "이벤트 신청 기간은 null이 되면 안됩니다.");
        Assert.notNull(eventPeriod, "이벤트 신청 기간은 null이 되면 안됩니다.");

        if (registrationPeriod.overlaps(eventPeriod)) {
            throw new BusinessRuleViolatedException("등록 기간과 이벤트 기간이 겹칠 수 없습니다.");
        }
        if (registrationPeriod.isAfter(eventPeriod)) {
            throw new BusinessRuleViolatedException("등록 기간은 이벤트 기간보다 앞서야 합니다.");
        }
    }
}
