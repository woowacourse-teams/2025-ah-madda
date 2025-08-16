package com.ahmadda.domain;


import com.ahmadda.domain.util.Assert;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_member_id")
    private Long id;

    @Column(nullable = false)
    private String nickname;

    // TODO. 추후에 @Async 사용을 고려하여 LAZY로 변경
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private OrganizationMember(
            final String nickname,
            final Member member,
            final Organization organization,
            final Role role
    ) {
        validateNickname(nickname);
        validateMember(member);
        validateOrganization(organization);
        validateRole(role);

        this.nickname = nickname;
        this.member = member;
        this.organization = organization;
        this.role = role;

        organization.getOrganizationMembers()
                .add(this);
    }

    public static OrganizationMember create(
            final String nickname,
            final Member member,
            final Organization organization,
            final Role role
    ) {
        return new OrganizationMember(nickname, member, organization, role);
    }

    public boolean isBelongTo(final Organization organization) {
        return this.organization.equals(organization);
    }

    public List<Event> getParticipatedEvents() {
        return organization.getEvents()
                .stream()
                .filter(event -> event.hasGuest(this))
                .toList();
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    private void validateNickname(final String nickname) {
        Assert.notBlank(nickname, "닉네임은 공백이면 안됩니다.");
    }

    private void validateMember(final Member member) {
        Assert.notNull(member, "멤버는 null이 되면 안됩니다.");
    }

    private void validateOrganization(final Organization organization) {
        Assert.notNull(organization, "조직은 null이 되면 안됩니다.");
    }

    private void validateRole(final Role role) {
        Assert.notNull(role, "조직원의 역할은 null이 되면 안됩니다.");
    }
}
