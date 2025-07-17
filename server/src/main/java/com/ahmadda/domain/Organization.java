package com.ahmadda.domain;


import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.domain.util.Assert;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Organization extends BaseEntity {

    private static final int MAX_DESCRIPTION_LENGTH = 2000;
    private static final int MAX_NAME_LENGTH = 20;
    private static final int MIN_DESCRIPTION_LENGTH = 2;
    private static final int MIN_NAME_LENGTH = 2;

    @Column(nullable = false)
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String name;


    private Organization(final String name, final String description, final String imageUrl) {
        this.name = Assert.notNull(name, "name null이 되면 안됩니다.");
        this.description = Assert.notNull(description, "description null이 되면 안됩니다.");
        this.imageUrl = Assert.notNull(imageUrl, "imageUrl null이 되면 안됩니다.");
        validate();
    }

    public static Organization create(final String name, final String description, final String imageUrl) {
        return new Organization(name, description, imageUrl);
    }

    private void validate() {
        if (name.isBlank() || name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            throw new BusinessRuleViolatedException(
                    String.format("이름의 길이는 %d자 이상 %d자 이하이어야 합니다.",
                                  MIN_NAME_LENGTH,
                                  MAX_NAME_LENGTH
                    )
            );
        }

        if (description.isBlank() || description.length() < 2 || description.length() > 2000) {
            throw new BusinessRuleViolatedException(
                    String.format("설명의 길이는 %d자 이상 %d자 이하이어야 합니다.",
                                  MIN_DESCRIPTION_LENGTH,
                                  MAX_DESCRIPTION_LENGTH
                    )
            );
        }
    }
}


