package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OrganizationCreateRequest;
import com.ahmadda.application.dto.OrganizationUpdateRequest;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.organization.InviteCode;
import com.ahmadda.domain.organization.InviteCodeRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationImageFile;
import com.ahmadda.domain.organization.OrganizationImageUploader;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import com.ahmadda.presentation.dto.OrganizationParticipateRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    public static final String WOOWACOURSE_NAME = "우아한테크코스";
    private static final String imageUrl = "techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/ah-madda/woowa.png";

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final MemberRepository memberRepository;
    private final InviteCodeRepository inviteCodeRepository;
    private final OrganizationImageUploader organizationImageUploader;

    @Transactional
    public Organization createOrganization(
            final OrganizationCreateRequest organizationCreateRequest,
            final OrganizationImageFile thumbnailOrganizationImageFile,
            final LoginMember loginMember
    ) {
        Member member = getMember(loginMember);

        String uploadImageUrl = organizationImageUploader.upload(thumbnailOrganizationImageFile);
        Organization organization = Organization.create(
                organizationCreateRequest.name(),
                organizationCreateRequest.description(),
                uploadImageUrl
        );
        organizationRepository.save(organization);

        OrganizationMember organizationMember =
                OrganizationMember.create(
                        organizationCreateRequest.nickname(),
                        member,
                        organization,
                        OrganizationMemberRole.ADMIN
                );
        organizationMemberRepository.save(organizationMember);

        return organization;
    }

    public Organization getOrganizationById(final Long organizationId) {
        return getOrganization(organizationId);
    }

    @Transactional
    public OrganizationMember participateOrganization(
            final Long organizationId,
            final LoginMember loginMember,
            final OrganizationParticipateRequest organizationParticipateRequest
    ) {
        validateAlreadyParticipationMember(organizationId, loginMember);

        Organization organization = getOrganization(organizationId);
        Member member = getMember(loginMember);
        InviteCode inviteCode = getInviteCode(organizationParticipateRequest.inviteCode());

        OrganizationMember organizationMember =
                organization.participate(
                        member,
                        organizationParticipateRequest.nickname(),
                        inviteCode,
                        LocalDateTime.now()
                );

        return organizationMemberRepository.save(organizationMember);
    }

    @Transactional
    public void updateOrganization(
            final Long organizationId,
            final OrganizationUpdateRequest organizationUpdateRequest,
            @Nullable final OrganizationImageFile thumbnailOrganizationImageFile,
            final LoginMember loginMember
    ) {
        Organization organization = getOrganization(organizationId);
        OrganizationMember updatingOrganizationMember = getOrganizationMember(organizationId, loginMember);

        String updateImageUrl = resolveUpdateImageUrl(organization.getImageUrl(), thumbnailOrganizationImageFile);

        organization.update(
                updatingOrganizationMember,
                organizationUpdateRequest.name(),
                organizationUpdateRequest.description(),
                updateImageUrl
        );
    }

    public List<Organization> getParticipatingOrganizations(final LoginMember loginMember) {
        Member member = getMember(loginMember);

        return organizationRepository.findMemberOrganizations(member);
    }

    @Transactional
    public void deleteOrganization(final Long organizationId, final LoginMember loginMember) {
        Organization organization = getOrganization(organizationId);
        OrganizationMember deletingMember = getOrganizationMember(organizationId, loginMember);

        validateAdmin(deletingMember);

        organizationRepository.delete(organization);
    }

    private void validateAdmin(final OrganizationMember organizationMember) {
        if (!organizationMember.isAdmin()) {
            throw new ForbiddenException("조직의 관리자만 삭제할 수 있습니다.");
        }
    }

    private String resolveUpdateImageUrl(
            final String imageUrl,
            final OrganizationImageFile thumbnailOrganizationImageFile
    ) {
        String updateImageUrl = imageUrl;

        if (thumbnailOrganizationImageFile != null) {
            updateImageUrl = organizationImageUploader.upload(thumbnailOrganizationImageFile);
        }

        return updateImageUrl;
    }

    private void validateAlreadyParticipationMember(final Long organizationId, final LoginMember loginMember) {
        if (organizationMemberRepository.existsByOrganizationIdAndMemberId(organizationId, loginMember.memberId())) {
            throw new UnprocessableEntityException("이미 참여한 조직입니다.");
        }
    }

    private Member getMember(final LoginMember loginMember) {
        return memberRepository.findById(loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다"));
    }

    private InviteCode getInviteCode(final String code) {
        return inviteCodeRepository.findByCode(code)
                .orElseThrow(() -> new UnprocessableEntityException("잘못된 초대코드입니다."));
    }

    private Organization getOrganization(final Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직입니다."));
    }

    private OrganizationMember getOrganizationMember(final Long organizationId, final LoginMember loginMember) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직원입니다."));
    }
}
