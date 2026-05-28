package br.com.matheusfragadev.lalouise.infra.security.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal
            (
                    @NonNull HttpServletRequest request,
                    @NonNull HttpServletResponse response,
                    @NonNull FilterChain filterChain
            )
            throws ServletException, IOException {

        String clientIp = getClientIp(request);
        String requestPath = request.getRequestURI();
        int maxRequests = getMaxRequestsForPath(requestPath);

        try {
            rateLimitService.validateRateLimit(clientIp, requestPath, maxRequests);
        } catch (RateLimitException e) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"mensagem\": \"" + e.getMessage() + "\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private int getMaxRequestsForPath(String path) {
        if (path.contains("/login")) return 5;
        if (path.contains("/admins")) return 100;
        if (path.contains("/labels")) return 50;
        return 30;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        return ip.split(",")[0].trim();
    }
}
