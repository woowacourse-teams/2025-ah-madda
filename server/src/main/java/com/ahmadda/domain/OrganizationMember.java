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

    private OrganizationMember(final String nickname, final Member member, final Organization organization) {
        this.nickname = Assert.notNull(nickname, "nickname null이 되면 안됩니다.");
        this.member = Assert.notNull(member, "member null이 되면 안됩니다.");
        this.organization = Assert.notNull(organization, "organization null이 되면 안됩니다.");
    }

    public static OrganizationMember create(final String nickname,
                                            final Member member,
                                            final Organization organization) {
        return new OrganizationMember(nickname, member, organization);
    }
}


