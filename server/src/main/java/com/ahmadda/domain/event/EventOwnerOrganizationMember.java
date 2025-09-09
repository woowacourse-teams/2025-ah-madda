package com.ahmadda.domain.event;

import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.domain.BaseEntity;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE event_owner_organization_member SET deleted_at = CURRENT_TIMESTAMP WHERE event_owner_organization_member_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_owner_event_id_organization_member_id",
                        columnNames = {"event_id", "organization_member_id"}
                )
        }
)
public class EventOwnerOrganizationMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_owner_organization_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_member_id", nullable = false)
    private OrganizationMember organizationMember;

    public EventOwnerOrganizationMember(final Event event, final OrganizationMember organizationMember) {
        validateIsSameOrganization(event, organizationMember);

        this.event = event;
        this.organizationMember = organizationMember;
    }

    public static EventOwnerOrganizationMember create(
            final Event event,
            final OrganizationMember organizationMember
    ) {
        return new EventOwnerOrganizationMember(event, organizationMember);
    }

    public boolean isBelongTo(final Organization organization) {
        return organizationMember.isBelongTo(organization);
    }

    public boolean isSameMember(final Member member) {
        return organizationMember.getMember()
                .equals(member);
    }

    public boolean isSameOrganizationMember(final OrganizationMember organizationMember) {
        return this.organizationMember.equals(organizationMember);
    }

    private void validateIsSameOrganization(final Event event, final OrganizationMember organizationMember) {
        if (!event.getOrganization()
                .isExistOrganizationMember(organizationMember)) {
            throw new ForbiddenException("자신과 공동 주최자는 동일한 이벤트 스페이스에 속해야 합니다.");
        }
    }
}
