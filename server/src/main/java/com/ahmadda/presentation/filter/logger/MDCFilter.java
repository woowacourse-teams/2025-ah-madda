package com.ahmadda.presentation.filter.logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCFilter implements Filter {

    private static final String REQUEST_ID = "request_id";

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        try {
            MDC.put(REQUEST_ID, generateRequestId());
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private String generateRequestId() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 8);
    }
}
