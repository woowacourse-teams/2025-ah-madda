package com.ahmadda.domain.event;


import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.BaseEntity;
import com.ahmadda.domain.organization.OrganizationMember;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE guest SET deleted_at = CURRENT_TIMESTAMP WHERE guest_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Guest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private OrganizationMember organizationMember;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "guest", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Answer> answers = new ArrayList<>();

    private Guest(final Event event, final OrganizationMember organizationMember, final LocalDateTime currentDateTime) {
        validateSameOrganization(event, organizationMember);

        this.event = event;
        this.organizationMember = organizationMember;

        event.participate(this, currentDateTime);
    }

    public static Guest create(
            final Event event,
            final OrganizationMember organizationMember,
            final LocalDateTime currentDateTime
    ) {
        return new Guest(event, organizationMember, currentDateTime);
    }

    public boolean isSameOrganizationMember(final OrganizationMember organizationMember) {
        return this.organizationMember.equals(organizationMember);
    }

    public void submitAnswers(final Map<Question, String> questionAnswers) {
        validateRequiredQuestions(questionAnswers);

        addAnswers(questionAnswers);
    }

    private void validateRequiredQuestions(final Map<Question, String> questionAnswers) {
        Set<Question> requiredQuestions = event.getRequiredQuestions();

        for (Question required : requiredQuestions) {
            String answer = questionAnswers.get(required);
            if (answer == null || answer.isBlank()) {
                throw new UnprocessableEntityException("필수 질문에 대한 답변이 누락되었습니다.");
            }
        }
    }

    private void addAnswers(final Map<Question, String> answers) {
        answers.forEach((question, answerText) -> {
            if (!event.hasQuestion(question)) {
                throw new UnprocessableEntityException("이벤트에 포함되지 않은 질문입니다.");
            }
            if (answerText == null || answerText.isBlank()) {
                return;
            }
            this.answers.add(Answer.create(question, this, answerText));
        });
    }

    private void validateSameOrganization(final Event event, final OrganizationMember organizationMember) {
        if (!organizationMember.isBelongTo(event.getOrganization())) {
            throw new UnprocessableEntityException("같은 조직의 이벤트에만 게스트로 참여가능합니다.");
        }
    }

    public List<Answer> viewAnswersAs(final OrganizationMember organizationMember) {
        if (!canViewAnswers(organizationMember)) {
            throw new ForbiddenException("답변을 볼 권한이 없습니다.");
        }

        return answers;
    }

    private boolean canViewAnswers(final OrganizationMember organizationMember) {
        return event.isOrganizer(organizationMember) || this.organizationMember.equals(organizationMember);
    }
}
