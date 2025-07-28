package com.ahmadda.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OrganizationCreateRequest;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.presentation.dto.ParticipateRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    public static final String WOOWACOURSE_NAME = "우아한테크코스";
    private static final String imageUrl = "techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/ah-madda/woowa.png";

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Organization createOrganization(final OrganizationCreateRequest organizationCreateRequest) {
        Organization organization = Organization.create(
                organizationCreateRequest.name(),
                organizationCreateRequest.description(),
                organizationCreateRequest.imageUrl()
        );

        return organizationRepository.save(organization);
    }

    public Organization getOrganization(final Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직입니다."));
    }

    public List<Event> getOrganizationEvents(final Long organizationId, final LoginMember loginMember) {
        Organization organization = getOrganization(organizationId);

        if (!organizationMemberRepository.existsByOrganizationIdAndMemberId(organizationId, loginMember.memberId())) {
            throw new AccessDeniedException("조직에 참여하지 않아 권한이 없습니다.");
        }

        return organization.getActiveEvents();
    }

    //TODO 07.25 이후 리팩터링 및 제거하기
    @Transactional
    @Deprecated
    public Organization alwaysGetWoowacourse() {
        Optional<Organization> organization = organizationRepository.findByName(WOOWACOURSE_NAME);

        return organization.orElseGet(() -> organizationRepository.save(
                Organization.create(
                        WOOWACOURSE_NAME,
                        "우아한테크코스입니다",
                        imageUrl
                )));
    }

    @Transactional
    public void participateOrganization(
            final Long organizationId,
            final LoginMember loginMember,
            final ParticipateRequestDto participateRequestDto
    ) {
        Long memberId = loginMember.memberId();
        if (organizationMemberRepository.existsByOrganizationIdAndMemberId(organizationId, memberId)) {
            throw new BusinessFlowViolatedException("이미 참여한 조직입니다.");
        }

        Organization organization = getOrganization(organizationId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다"));

        OrganizationMember organizationMember =
                OrganizationMember.create(participateRequestDto.nickname(), member, organization);

        organizationMemberRepository.save(organizationMember);
    }
}
