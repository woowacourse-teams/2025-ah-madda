package com.ahmadda.domain.notification;

import com.ahmadda.domain.BaseEntity;
import com.ahmadda.domain.organization.OrganizationMember;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE reminder_recipient SET deleted_at = CURRENT_TIMESTAMP WHERE reminder_recipient_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class ReminderRecipient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reminder_recipient_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_member_id", nullable = false)
    private OrganizationMember organizationMember;

    private ReminderRecipient(final OrganizationMember organizationMember) {
        this.organizationMember = organizationMember;
    }

    public static ReminderRecipient create(final OrganizationMember organizationMember) {
        return new ReminderRecipient(organizationMember);
    }
}
