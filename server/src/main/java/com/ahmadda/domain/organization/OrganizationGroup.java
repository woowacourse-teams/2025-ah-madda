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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "organization_group")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE organization_group SET deleted_at = CURRENT_TIMESTAMP WHERE organization_group_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class OrganizationGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_group_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    private OrganizationGroup(final String name, final Organization organization) {
        this.name = name;
        this.organization = organization;
    }

    public static OrganizationGroup create(
            final String name,
            final Organization organization,
            final OrganizationMember creator
    ) {
        if (!creator.isBelongTo(organization)) {
            throw new ForbiddenException("이벤트 스페이스의 구성원만 그룹을 만들 수 있습니다.");
        }
        if (!creator.isAdmin()) {
            throw new ForbiddenException("어드민만 그룹을 만들 수 있습니다.");
        }

        return new OrganizationGroup(name, organization);
    }
}
