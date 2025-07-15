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
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private Member(final String name, final String email) {
        this.name = Assert.notNull(name, "name null이 되면 안됩니다.");
        this.email = Assert.notNull(email, "email null이 되면 안됩니다.");
    }

    public static Member create(final String name, final String email) {
        return new Member(name, email);
    }
}
