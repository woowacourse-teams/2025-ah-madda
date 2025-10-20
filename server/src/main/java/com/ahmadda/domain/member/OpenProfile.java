package com.ahmadda.domain.member;

import com.ahmadda.domain.BaseEntity;
import com.ahmadda.domain.organization.OrganizationGroup;
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
@Table(name = "open_profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE open_profile SET deleted_at = CURRENT_TIMESTAMP WHERE open_profile_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class OpenProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "open_profile_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_group_id", nullable = false)
    private OrganizationGroup organizationGroup;

    private OpenProfile(
            final Member member,
            final OrganizationGroup organizationGroup
    ) {
        this.member = member;
        this.organizationGroup = organizationGroup;
    }

    public static OpenProfile create(
            final Member member,
            final OrganizationGroup organizationGroup
    ) {
        return new OpenProfile(member, organizationGroup);
    }

    public void updateProfile(final OrganizationGroup organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public String getName() {
        return member.getName();
    }

    public String getEmail() {
        return member.getEmail();
    }

    public String getPicture() {
        return member.getProfileImageUrl();
    }
}