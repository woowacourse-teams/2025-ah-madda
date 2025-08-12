package com.ahmadda.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    boolean existsByIdAndMemberId(Long templateId, Long memberId);

    List<Template> findAllByMemberId(Long memberId);
}
