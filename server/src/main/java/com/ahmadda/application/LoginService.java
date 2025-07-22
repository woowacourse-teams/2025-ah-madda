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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final GoogleOAuthProvider googleOAuthProvider;
    private final JwtTokenProvider jwtTokenProvider;
    //TODO 07.25이후 사용하지않으면 삭제
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Transactional
    public String login(final String code) {
        OAuthUserInfoResponse userInfo = googleOAuthProvider.getUserInfo(code);

        Member member = findOrCreateMember(userInfo.name(), userInfo.email());
        addMemberToWoowacourse(member);
        return jwtTokenProvider.createToken(member);
    }

    private Member findOrCreateMember(final String name, final String email) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Member newMember = Member.create(name, email);

                    return memberRepository.save(newMember);
                });
    }

    @Deprecated
    @Transactional
    //TODO 07.25 이후 리팩터링 및 제거하기
    public void addMemberToWoowacourse(final Member member) {
        Optional<Organization> findOrganization =
                organizationRepository.findByName("우아한테크코스");

        Organization organization =
                findOrganization.orElseGet(() -> organizationRepository.save(
                        Organization.create("우아한테크코스",
                                            "우아한테크코스입니당딩동",
                                            "imageUrl"
                        )
                ));

        Optional<OrganizationMember> existOrganizationMember = organization.getOrganizationMembers()
                .stream()
                .filter((findOrganizationMember) -> findOrganizationMember.getMember().isSameMember(member.getId()))
                .findAny();

        if (existOrganizationMember.isEmpty()) {
            OrganizationMember organizationMember = OrganizationMember.create(member.getName(), member, organization);
            OrganizationMember savedOrganizationMember = organizationMemberRepository.save(organizationMember);
        }
    }
}
