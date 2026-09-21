package ru.prohor.universe.scarif.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.prohor.universe.scarif.jwt.MDCFields;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(-200)
public class LogRequestContextFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        String method = request.getMethod();
        String url = request.getRequestURI();
        MDC.put(MDCFields.REQUEST_ID_KEY, requestId);
        MDC.put(MDCFields.METHOD_KEY, method);
        MDC.put(MDCFields.REQUEST_URL_KEY, url);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
