package com.ahmadda.presentation.header;

import com.ahmadda.common.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

@Component
public class HeaderProvider {

    private static final String BEARER_TYPE = "Bearer ";

    public String extractAccessToken(final String header) {
        if (header != null && header.startsWith(BEARER_TYPE)) {
            return header.substring(BEARER_TYPE.length())
                    .trim();
        }

        throw new UnauthorizedException("인증 토큰 정보가 존재하지 않거나 유효하지 않습니다.");
    }
}
