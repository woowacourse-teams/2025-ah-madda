package com.ahmadda.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    List<Guest> findAllByParticipant_Member_IdAndParticipant_Organization_Id(Long memberId, Long organizationId);
}
