package com.ahmadda.application;

import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.infra.jwt.JwtTokenProvider;
import com.ahmadda.infra.oauth.GoogleOAuthProvider;
import com.ahmadda.infra.oauth.dto.OAuthUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private static final String imageUrl = "techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/ah-madda/woowa.png";

    private final MemberRepository memberRepository;
    private final GoogleOAuthProvider googleOAuthProvider;
    private final JwtTokenProvider jwtTokenProvider;
    //TODO 07.25이후 사용하지않으면 삭제
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public String login(final String code) {
        OAuthUserInfoResponse userInfo = googleOAuthProvider.getUserInfo(code);

        Member member = findOrCreateMember(userInfo.name(), userInfo.email());
        addMemberToWoowacourse(member);
        return jwtTokenProvider.createToken(member.getId());
    }

    private Member findOrCreateMember(final String name, final String email) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Member newMember = Member.create(name, email);

                    eventPublisher.publishEvent(newMember);

                    return memberRepository.save(newMember);
                });
    }

    @Deprecated
    @Transactional
    //TODO 07.25 이후 리팩터링 및 제거하기
    public void addMemberToWoowacourse(final Member member) {
        Optional<Organization> findOrganization =
                organizationRepository.findByName(OrganizationService.WOOWACOURSE_NAME);

        Organization organization =
                findOrganization.orElseGet(() -> organizationRepository.save(
                        Organization.create(OrganizationService.WOOWACOURSE_NAME,
                                            "우아한테크코스입니다",
                                            imageUrl
                        )
                ));

        Optional<OrganizationMember> existOrganizationMember =
                organizationMemberRepository.findByOrganizationIdAndMemberId(organization.getId(), member.getId());

        if (existOrganizationMember.isEmpty()) {
            OrganizationMember organizationMember = OrganizationMember.create(member.getName(), member, organization);
            OrganizationMember savedOrganizationMember = organizationMemberRepository.save(organizationMember);
        }
    }
}
