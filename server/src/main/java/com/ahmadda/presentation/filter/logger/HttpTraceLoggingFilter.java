package com.ahmadda.presentation.filter.logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpTraceLoggingFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "trace_id";

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain chain
    ) throws ServletException, IOException {
        try {
            MDC.put(TRACE_ID, generateTraceId());
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private String generateTraceId() {
        return "http-" + UUID.randomUUID()
                .toString()
                .substring(0, 8);
    }
}
