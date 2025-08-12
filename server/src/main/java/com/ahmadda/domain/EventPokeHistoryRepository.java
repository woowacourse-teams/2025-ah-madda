package com.ahmadda.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventPokeHistoryRepository extends JpaRepository<EventPokeHistory, Long> {

    //TODO 성능 매우 나쁨!! 반드시 개선 필요
    //TODO. 우선 빠르게 구현후 인덱싱 혹은 메모리 방식으로 전환 논의 필요
    int countEventPokeHistoryByEventAndSenderAndRecipient(
            final Event event,
            final OrganizationMember sender,
            final OrganizationMember recipient
    );
}
