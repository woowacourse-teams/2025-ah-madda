package com.ahmadda.presentation.filter.logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final List<String> SENSITIVE_HEADERS = List.of(
            "authorization", "cookie", "set-cookie"
    );

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain chain
    ) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        chain.doFilter(requestWrapper, responseWrapper);

        log.info(
                "[Request] method={} uri={} headers=[{}] body={}",
                request.getMethod(),
                request.getRequestURI(),
                buildRequestHeadersInline(requestWrapper),
                getBodyAsUtf8String(requestWrapper.getContentAsByteArray())
        );

        log.info(
                "[Response] status={} body={}",
                response.getStatus(),
                getBodyAsUtf8String(responseWrapper.getContentAsByteArray())
        );

        responseWrapper.copyBodyToResponse();
    }

    private String buildRequestHeadersInline(final HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return "";
        }

        return Collections.list(headerNames)
                .stream()
                .map(headerName -> {
                    String value = request.getHeader(headerName);
                    if (isSensitiveHeader(headerName)) {
                        value = "*****";
                    }
                    return headerName + "=" + value;
                })
                .collect(Collectors.joining(", "));
    }

    private boolean isSensitiveHeader(final String headerName) {
        return SENSITIVE_HEADERS.stream()
                .anyMatch(sensitive -> sensitive.equalsIgnoreCase(headerName));
    }

    private String getBodyAsUtf8String(final byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        String path = request.getRequestURI();
        
        return path.contains("/api-docs")
                || path.contains("/swagger")
                || path.contains("/specification")
                || path.contains("/favicon.ico")
                || path.contains("/actuator");
    }
}
