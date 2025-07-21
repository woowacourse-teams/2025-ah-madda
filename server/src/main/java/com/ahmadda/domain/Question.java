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
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String questionText;

    @Column(nullable = false)
    private boolean isRequired;

    @Column(nullable = false)
    private int orderIndex;

    private Question(
            final Event event,
            final String questionText,
            final boolean isRequired,
            final int orderIndex
    ) {
        validateEvent(event);
        validateQuestionText(questionText);

        this.event = event;
        this.questionText = questionText;
        this.isRequired = isRequired;
        this.orderIndex = orderIndex;
        
        event.getQuestions()
                .add(this);
    }

    public static Question create(
            final Event event,
            final String questionText,
            final boolean isRequired,
            final int orderIndex
    ) {
        return new Question(event, questionText, isRequired, orderIndex);
    }

    private void validateEvent(final Event event) {
        Assert.notNull(event, "이벤트는 null이 되면 안됩니다.");
    }

    private void validateQuestionText(final String questionText) {
        Assert.notBlank(questionText, "질문은 공백이면 안됩니다.");
    }
}
