package com.ahmadda.application.dto;

import com.ahmadda.domain.Period;

public record EventCreateRequest(
        String title,
        String description,
        String place,
        Period registrationPeriod,
        Period eventPeriod,
        int maxCapacity,
        Long organizerId,
        Long organizationId
) {

}
