package com.ahmadda.domain;

import com.ahmadda.domain.util.Assert;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReminderRecipient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reminder_recipient_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_member_id", nullable = false)
    private OrganizationMember organizationMember;

    private ReminderRecipient(final OrganizationMember organizationMember) {
        validateOrganizationMember(organizationMember);
        this.organizationMember = organizationMember;
    }

    public static ReminderRecipient create(final OrganizationMember organizationMember) {
        return new ReminderRecipient(organizationMember);
    }

    private void validateOrganizationMember(final OrganizationMember organizationMember) {
        Assert.notNull(organizationMember, "리마인더 수신자의 조직원이 null일 수 없습니다.");
    }
}
