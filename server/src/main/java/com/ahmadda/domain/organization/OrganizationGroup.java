package com.ahmadda.domain.organization;

import com.ahmadda.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private OrganizationGroup(final String name) {
        this.name = name;
    }

    public static OrganizationGroup create(final String name) {
        return new OrganizationGroup(name);
    }
}
