package com.ahmadda.domain.event;

import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.UnprocessableEntityException;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE event_organizer SET deleted_at = CURRENT_TIMESTAMP WHERE event_organizer_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "event_organizer")
public class EventOrganizer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_organizer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_member_id", nullable = false)
    private OrganizationMember organizationMember;

    public EventOrganizer(final Event event, final OrganizationMember organizationMember) {
        validateIsInSameOrganization(event, organizationMember);

        this.event = event;
        this.organizationMember = organizationMember;
    }

    public static EventOrganizer create(
            final Event event,
            final OrganizationMember organizationMember
    ) {
        return new EventOrganizer(event, organizationMember);
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

    public void approve(final Guest guest) {
        if (!event.isApprovalRequired()) {
            throw new UnprocessableEntityException("승인 가능한 이벤트가 아니라 승인 상태를 변경할 수 없습니다.");
        }

        if (event.isFull()) {
            throw new UnprocessableEntityException("수용 인원이 가득차 해당 게스트를 승인할 수 없습니다.");
        }

        guest.changeApprovalStatus(ApprovalStatus.APPROVED);
    }

    public void reject(final Guest guest) {
        if (!event.isApprovalRequired()) {
            throw new UnprocessableEntityException("승인 가능한 이벤트가 아니라 승인 상태를 변경할 수 없습니다.");
        }

        guest.changeApprovalStatus(ApprovalStatus.REJECTED);
    }

    private void validateIsInSameOrganization(final Event event, final OrganizationMember organizationMember) {
        if (!event.getOrganization()
                .isExistOrganizationMember(organizationMember)) {
            throw new ForbiddenException("주최자는 동일한 이벤트 스페이스에 속해야 합니다.");
        }
    }

    public String getNickname() {
        return organizationMember.getNickname();
    }
}
