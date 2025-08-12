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

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReminderHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reminder_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_member_id", nullable = false)
    private OrganizationMember organizationMember;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    private ReminderHistory(
            final Event event,
            final OrganizationMember organizationMember,
            final String content,
            final LocalDateTime sentAt
    ) {
        validateEvent(event);
        validateOrganizationMember(organizationMember);
        validateContent(content);
        validateSentAt(sentAt);

        this.event = event;
        this.organizationMember = organizationMember;
        this.content = content;
        this.sentAt = sentAt;
    }

    public static ReminderHistory create(
            final Event event,
            final OrganizationMember organizationMember,
            final String content,
            final LocalDateTime sentAt
    ) {
        return new ReminderHistory(event, organizationMember, content, sentAt);
    }

    public static ReminderHistory createNow(
            final Event event,
            final OrganizationMember organizationMember,
            final String content
    ) {
        return new ReminderHistory(event, organizationMember, content, LocalDateTime.now());
    }

    private void validateEvent(final Event event) {
        Assert.notNull(event, "리마인더 히스토리의 이벤트가 null일 수 없습니다.");
    }

    private void validateOrganizationMember(final OrganizationMember organizationMember) {
        Assert.notNull(organizationMember, "리마인더 히스토리의 조직원이 null일 수 없습니다.");
    }

    private void validateContent(final String content) {
        Assert.notBlank(content, "리마인더 히스토리의 콘텐츠는 공백일 수 없습니다.");
    }

    private void validateSentAt(final LocalDateTime sentAt) {
        Assert.notNull(sentAt, "리마인더 발송 시각이 null일 수 없습니다.");
    }
}
