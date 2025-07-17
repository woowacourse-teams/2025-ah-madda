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

    private void validateName(final String name) {
        Assert.notBlank(name, "name은 공백이면 안됩니다.");

        if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            throw new BusinessRuleViolatedException(
                    String.format("이름의 길이는 %d자 이상 %d자 이하이어야 합니다.",
                            MIN_NAME_LENGTH,
                            MAX_NAME_LENGTH
                    )
            );
        }
    }

    private void validateDescription(final String description) {
        Assert.notBlank(description, "description은 공백이면 안됩니다.");

        if (description.length() < 2 || description.length() > 2000) {
            throw new BusinessRuleViolatedException(
                    String.format("설명의 길이는 %d자 이상 %d자 이하이어야 합니다.",
                            MIN_DESCRIPTION_LENGTH,
                            MAX_DESCRIPTION_LENGTH
                    )
            );
        }
    }

    private void validateImageUrl(final String imageUrl) {
        Assert.notBlank(imageUrl, "imageUrl은 공백이면 안됩니다.");
    }
}
