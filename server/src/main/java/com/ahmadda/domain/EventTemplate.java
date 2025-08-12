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
public class EventTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_template_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    private EventTemplate(final Member member, final String title, final String description) {
        validateMember(member);
        validateTitle(title);
        validateDescription(description);

        this.member = member;
        this.title = title;
        this.description = description;
    }

    public static EventTemplate create(final Member member, final String title, final String description) {
        return new EventTemplate(member, title, description);
    }

    private void validateMember(final Member member) {
        Assert.notNull(member, "멤버는 null이 되면 안됩니다.");
    }

    private void validateTitle(final String title) {
        Assert.notBlank(title, "제목은 공백이면 안됩니다.");
    }

    private void validateDescription(final String description) {
        Assert.notBlank(description, "설명은 공백이면 안됩니다.");
    }
}
