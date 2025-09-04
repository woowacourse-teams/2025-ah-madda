package com.ahmadda.domain.event;


import com.ahmadda.domain.BaseEntity;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE answer SET deleted_at = CURRENT_TIMESTAMP WHERE answer_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(nullable = false)
    private String answerText;

    private Answer(final Question question, final Guest guest, final String answerText) {
        validateQuestion(question);
        validateQuest(guest);
        validateAnswerText(answerText);

        this.question = question;
        this.guest = guest;
        this.answerText = answerText;
    }

    public static Answer create(final Question question, final Guest guest, final String answerText) {
        return new Answer(question, guest, answerText);
    }

    private void validateQuestion(final Question question) {
        Assert.notNull(question, "질문은 null이 되면 안됩니다.");
    }

    private void validateQuest(final Guest guest) {
        Assert.notNull(guest, "게스트는 null이 되면 안됩니다.");
    }

    private void validateAnswerText(final String answerText) {
        Assert.notBlank(answerText, "답변은 공백이면 안됩니다.");
    }
}

