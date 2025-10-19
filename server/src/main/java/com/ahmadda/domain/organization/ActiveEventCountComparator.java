package com.ahmadda.domain.organization;

import java.time.LocalDateTime;
import java.util.Comparator;

public class ActiveEventCountComparator implements Comparator<Organization> {

    private final LocalDateTime now;

    public ActiveEventCountComparator(final LocalDateTime now) {
        this.now = now;
    }

    @Override
    public int compare(final Organization o1, final Organization o2) {
        return Integer.compare(
                o2.getActiveEventsCount(now),
                o1.getActiveEventsCount(now)
        );
    }
}
