package com.ahmadda.domain.event;


import com.ahmadda.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE question SET deleted_at = CURRENT_TIMESTAMP WHERE question_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(nullable = false)
    private String questionText;

    @Column(nullable = false)
    private boolean isRequired;

    @Column(nullable = false)
    private int orderIndex;

    private Question(
            final String questionText,
            final boolean isRequired,
            final int orderIndex
    ) {
        this.questionText = questionText;
        this.isRequired = isRequired;
        this.orderIndex = orderIndex;
    }

    public static Question create(
            final String questionText,
            final boolean isRequired,
            final int orderIndex
    ) {
        return new Question(questionText, isRequired, orderIndex);
    }
}
