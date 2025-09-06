package com.ahmadda.domain.organization;

import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.domain.BaseEntity;
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

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE invite_code SET deleted_at = CURRENT_TIMESTAMP WHERE invite_code_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class InviteCode extends BaseEntity {

    private static final int DEFAULT_EXPIRE_DAYS = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invite_code_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private OrganizationMember inviter;

    private InviteCode(
            final String code,
            final LocalDateTime expiresAt,
            final Organization organization,
            final OrganizationMember inviter
    ) {
        this.code = code;
        this.expiresAt = expiresAt;
        this.organization = organization;
        this.inviter = inviter;
    }

    public static InviteCode create(
            final String code,
            final Organization organization,
            final OrganizationMember inviter,
            final LocalDateTime currentDateTime
    ) {
        validateBelongToOrganization(organization, inviter);

        LocalDateTime expiresAt = currentDateTime.plusDays(DEFAULT_EXPIRE_DAYS);

        return new InviteCode(code, expiresAt, organization, inviter);
    }

    public boolean isExpired(final LocalDateTime currentDateTime) {
        return currentDateTime.isAfter(expiresAt);
    }

    public boolean matchesOrganization(final Organization organization) {
        return this.organization.equals(organization);
    }

    private static void validateBelongToOrganization(
            final Organization organization,
            final OrganizationMember organizationMember
    ) {
        if (!organizationMember.isBelongTo(organization)) {
            throw new ForbiddenException("조직에 참여중인 구성원만 해당 조직의 초대코드를 만들 수 있습니다.");
        }
    }
}
