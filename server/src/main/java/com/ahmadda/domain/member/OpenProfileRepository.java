package com.ahmadda.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpenProfileRepository extends JpaRepository<OpenProfile, Long> {

    Optional<OpenProfile> findByMemberId(final Long memberId);
}
