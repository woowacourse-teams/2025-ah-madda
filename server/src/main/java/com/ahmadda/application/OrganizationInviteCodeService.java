package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.organization.InviteCode;
import com.ahmadda.domain.organization.InviteCodeRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationRepository;
import com.ahmadda.infra.generator.RandomCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrganizationInviteCodeService {

    private static final int INVITE_CODE_LENGTH = 6;

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final InviteCodeRepository inviteCodeRepository;
    private final RandomCodeGenerator randomCodeGenerator;

    @Transactional
    public InviteCode createInviteCode(
            final Long organizationId,
            final LoginMember loginMember,
            final LocalDateTime now
    ) {
        Organization organization = getOrganization(organizationId);
        OrganizationMember inviter = getOrganizationMember(organizationId, loginMember);

        return findOrCreateInviteCode(inviter, organization, now);
    }

    public Organization getOrganizationByCode(final String code) {
        InviteCode inviteCode = inviteCodeRepository.findByCode(code)
                .orElseThrow(() -> new BusinessFlowViolatedException("유효하지 않은 초대코드입니다."));

        if (inviteCode.isExpired(LocalDateTime.now())) {
            throw new BusinessFlowViolatedException("만료된 초대코드입니다.");
        }

        return inviteCode.getOrganization();
    }

    private Organization getOrganization(final Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직 정보입니다."));
    }

    private OrganizationMember getOrganizationMember(final Long organizationId, final LoginMember loginMember) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직원 정보입니다."));
    }

    private InviteCode findOrCreateInviteCode(
            final OrganizationMember inviter,
            final Organization organization,
            final LocalDateTime now
    ) {
        return inviteCodeRepository.findFirstByInviterAndExpiresAtAfter(inviter, now)
                .orElseGet(() -> {
                    InviteCode inviteCode = InviteCode.create(
                            randomCodeGenerator.generate(INVITE_CODE_LENGTH),
                            organization,
                            inviter,
                            now
                    );

                    return inviteCodeRepository.save(inviteCode);
                });
    }
}
