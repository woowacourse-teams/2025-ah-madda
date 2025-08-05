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

    @Column(nullable = false)
    private String picture;

    private Member(final String name, final String email) {
        validateName(name);
        validateEmail(email);

        this.name = name;
        this.email = email;
    }

    //todo: 생성자 물어보기
    private Member(final String name, final String email, final String picture) {
        validateName(name);
        validateEmail(email);

        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public static Member create(final String name, final String email) {
        return new Member(name, email);
    }

    public static Member create(final String name, final String email, final String userProfile) {
        return new Member(name, email, userProfile);
    }

    private void validateName(final String name) {
        Assert.notBlank(name, "이름은 공백이면 안됩니다.");
    }

    private void validateEmail(final String email) {
        Assert.notBlank(email, "이메일은 공백이면 안됩니다.");
    }
}
