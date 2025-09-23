package com.ahmadda.domain.organization;


import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.domain.BaseEntity;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.member.Member;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE organization_member SET deleted_at = CURRENT_TIMESTAMP WHERE organization_member_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class OrganizationMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_member_id")
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationMemberRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_group_id", nullable = false)
    private OrganizationGroup group;

    private OrganizationMember(
            final String nickname,
            final Member member,
            final Organization organization,
            final OrganizationMemberRole role,
            final OrganizationGroup group
    ) {
        this.nickname = nickname;
        this.member = member;
        this.organization = organization;
        this.role = role;
        this.group = group;

        organization.getOrganizationMembers()
                .add(this);
    }

    public static OrganizationMember create(
            final String nickname,
            final Member member,
            final Organization organization,
            final OrganizationMemberRole role,
            final OrganizationGroup group
    ) {
        return new OrganizationMember(nickname, member, organization, role, group);
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
        return this.role == OrganizationMemberRole.ADMIN;
    }

    public void changeRolesOf(final List<OrganizationMember> targets, final OrganizationMemberRole newRole) {
        if (!isAdmin()) {
            throw new ForbiddenException("관리자만 구성원의 권한을 변경할 수 있습니다.");
        }

        for (final OrganizationMember target : targets) {
            if (!target.isBelongTo(this.organization)) {
                throw new ForbiddenException("같은 이벤트 스페이스에 속한 구성원만 권한을 변경할 수 있습니다.");
            }

            target.role = newRole;
        }
    }

    public void rename(final String newNickname) {
        this.nickname = newNickname;
    }

    public void update(final String nickname, final OrganizationGroup group) {
        this.nickname = nickname;
        this.group = group;
    }

    public boolean isEqualNickname(final String nickname) {
        return this.nickname.equals(nickname);
    }
}
