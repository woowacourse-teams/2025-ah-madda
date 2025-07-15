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
        this.question = Assert.notNull(question, "question null이 되면 안됩니다.");
        this.guest = Assert.notNull(guest, "guest null이 되면 안됩니다.");
        this.answerText = Assert.notNull(answerText, "answerText null이 되면 안됩니다.");
    }

    public static Answer create(final Question question, final Guest guest, final String answerText) {
        return new Answer(question, guest, answerText);
    }
}

