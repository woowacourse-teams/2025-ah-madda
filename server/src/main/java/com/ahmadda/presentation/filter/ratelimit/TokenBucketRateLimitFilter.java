package com.ahmadda.presentation.filter.ratelimit;

import com.ahmadda.infra.auth.jwt.JwtProvider;
import com.ahmadda.infra.auth.jwt.config.JwtAccessTokenProperties;
import com.ahmadda.presentation.header.HeaderProvider;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.mysql.MySQLSelectForUpdateBasedProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@EnableConfigurationProperties(JwtAccessTokenProperties.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
@RequiredArgsConstructor
public class TokenBucketRateLimitFilter extends OncePerRequestFilter {

    private final MySQLSelectForUpdateBasedProxyManager<Long> proxyManager;
    private final BucketConfiguration memberRateLimitConfig;
    private final RateLimitExceededHandler rateLimitExceededHandler;
    private final HeaderProvider headerProvider;
    private final JwtProvider jwtProvider;
    private final JwtAccessTokenProperties jwtAccessTokenProperties;

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain chain
    ) throws IOException, ServletException {
        Optional<Long> memberId = resolveMemberId(request);

        if (memberId.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        if (processRateLimiting(memberId.get(), request, response)) {
            return;
        }

        chain.doFilter(request, response);
    }

    private Optional<Long> resolveMemberId(final HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String accessToken = headerProvider.extractAccessToken(authorizationHeader);
            Long memberId = jwtProvider.parsePayload(accessToken, jwtAccessTokenProperties.getAccessSecretKey())
                    .memberId();

            return Optional.of(memberId);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private boolean processRateLimiting(
            final Long memberId,
            final HttpServletRequest request,
            final HttpServletResponse response
    ) throws IOException {
        BucketProxy bucket = proxyManager.getProxy(memberId, () -> memberRateLimitConfig);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            return false;
        }

        long retryAfterSeconds = Math.max(
                1,
                TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill())
        );
        rateLimitExceededHandler.handle(request, response, retryAfterSeconds);
        return true;
    }
}
