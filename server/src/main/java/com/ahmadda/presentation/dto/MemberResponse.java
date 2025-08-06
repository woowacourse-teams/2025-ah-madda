package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Member;

public record MemberResponse(
        Long id,
        String name,
        String email,
        String picture
) {

    public static MemberResponse from(final Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getProfileImageUrl()
        );
    }
}
