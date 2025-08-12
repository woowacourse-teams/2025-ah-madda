package com.ahmadda.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    boolean existsByIdAndMemberId(final Long templateId, final Long memberId);

    List<Template> findAllByMemberId(final Long memberId);
}
