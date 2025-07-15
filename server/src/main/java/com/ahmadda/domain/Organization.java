package com.ahmadda.domain;


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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    private Organization(final String name, final String description, final String imageUrl) {
        this.name = Assert.notNull(name, "name null이 되면 안됩니다.");
        this.description = Assert.notNull(description, "description null이 되면 안됩니다.");
        this.imageUrl = Assert.notNull(imageUrl, "imageUrl null이 되면 안됩니다.");
    }

    public static Organization create(final String name, final String description, final String imageUrl) {
        return new Organization(name, description, imageUrl);
    }
}


