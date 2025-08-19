package com.ahmadda.domain;


import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.domain.exception.UnauthorizedOperationException;
import com.ahmadda.domain.util.Assert;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Organization extends BaseEntity {

    private static final int MAX_DESCRIPTION_LENGTH = 30;
    private static final int MAX_NAME_LENGTH = 30;
    private static final int MIN_DESCRIPTION_LENGTH = 1;
    private static final int MIN_NAME_LENGTH = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organization")
    private final List<Event> events = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organization")
    private final List<OrganizationMember> organizationMembers = new ArrayList<>();

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String name;

    private Organization(final String name, final String description, final String imageUrl) {
        validateName(name);
        validateDescription(description);
        validateImageUrl(imageUrl);

        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public static Organization create(final String name, final String description, final String imageUrl) {
        return new Organization(name, description, imageUrl);
    }

    public void addEvent(final Event event) {
        this.events.add(event);
    }

    public List<Event> getActiveEvents(final LocalDateTime currentDateTime) {
        return events.stream()
                .filter((event) -> event.isRegistrationEnd(currentDateTime))
                .toList();
    }

    public OrganizationMember participate(
            final Member member,
            final String nickname,
            final InviteCode inviteCode,
            final LocalDateTime now
    ) {
        if (!inviteCode.matchesOrganization(this)) {
            throw new BusinessRuleViolatedException("잘못된 초대코드입니다.");
        }
        if (inviteCode.isExpired(now)) {
            throw new BusinessRuleViolatedException("초대코드가 만료되었습니다.");
        }

        return OrganizationMember.create(nickname, member, this, Role.USER);
    }

    public boolean isExistOrganizationMember(final OrganizationMember otherOrganizationMember) {
        return organizationMembers.contains(otherOrganizationMember);
    }

    public void update(
            final OrganizationMember updatingOrganizationMember,
            final String name,
            final String description,
            final String imageUrl
    ) {
        validateUpdatableBy(updatingOrganizationMember);
        validateName(name);
        validateDescription(description);
        validateImageUrl(imageUrl);

        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    private void validateUpdatableBy(final OrganizationMember updatingOrganizationMember) {
        if (!updatingOrganizationMember.isBelongTo(this)) {
            throw new UnauthorizedOperationException("조직에 속한 조직원만 수정이 가능합니다.");
        }

        if (!updatingOrganizationMember.isAdmin()) {
            throw new UnauthorizedOperationException("조직원의 관리자만 조직 정보를 수정할 수 있습니다.");
        }
    }

    private void validateName(final String name) {
        Assert.notBlank(name, "이름은 공백이면 안됩니다.");

        if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            throw new BusinessRuleViolatedException(
                    String.format(
                            "이름의 길이는 %d자 이상 %d자 이하이어야 합니다.",
                            MIN_NAME_LENGTH,
                            MAX_NAME_LENGTH
                    )
            );
        }
    }

    private void validateDescription(final String description) {
        Assert.notBlank(description, "설명은 공백이면 안됩니다.");

        if (description.length() < MIN_DESCRIPTION_LENGTH || description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new BusinessRuleViolatedException(
                    String.format(
                            "설명의 길이는 %d자 이상 %d자 이하이어야 합니다.",
                            MIN_DESCRIPTION_LENGTH,
                            MAX_DESCRIPTION_LENGTH
                    )
            );
        }
    }

    private void validateImageUrl(final String imageUrl) {
        Assert.notBlank(imageUrl, "이미지 url은 공백이면 안됩니다.");
    }
}
