package com.ahmadda.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpenProfileRepository extends JpaRepository<OpenProfile, Long> {

    List<OpenProfile> findByMember(Member member);
}