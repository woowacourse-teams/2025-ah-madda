package com.ahmadda.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventViewMatric {

    @Id
    private Long id;

    private LocalDateTime viewDate;

    private Integer viewCount;

    public EventViewMatric(final LocalDateTime viewDate) {
        this.viewDate = viewDate;
        viewCount = 0;
    }

    public static EventViewMatric create(final LocalDateTime viewDate) {
        return new EventViewMatric(viewDate);
    }

    public boolean isAfter(LocalDateTime currentDateTime) {
        return viewDate.isAfter(currentDateTime);
    }
}

