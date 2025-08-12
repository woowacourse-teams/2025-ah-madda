package com.ahmadda.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventTemplateRepository extends JpaRepository<EventTemplate, Long> {

    boolean existsByIdAndMemberId(final Long templateId, final Long memberId);

    List<EventTemplate> findAllByMemberId(final Long memberId);
}
