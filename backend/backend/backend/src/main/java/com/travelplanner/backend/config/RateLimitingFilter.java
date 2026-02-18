package com.travelplanner.backend.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Cache<String, Bucket> bucketCache = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS) // Auto-clean old entries
            .maximumSize(10_000) // Limit memory usage
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = getClientIp(request);
        String path = request.getRequestURI();

        // Get or create bucket
        Bucket bucket = getBucket(clientIp, path);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            // Don't write twice - remove the first write
            response.getWriter().write(
                    "{\"success\":false,\"message\":\"Rate limit exceeded. Please try again later.\"}"
            );
        }
    }

    private Bucket getBucket(String clientIp, String path) {
        String key = clientIp + ":" + path;

        // Use Caffeine cache's get method, NOT Map's computeIfAbsent
        return bucketCache.get(key, k -> {
            Bandwidth limit;

            // Apply different limits based on endpoint
            if (path.contains("/api/users/login") || path.contains("/api/users/register")) {
                // Stricter limits for auth endpoints
                limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
            } else if (path.contains("/api/admin")) {
                // Admin endpoints have higher limits
                limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
            } else {
                // Default limits for other endpoints
                limit = Bandwidth.classic(60, Refill.intervally(60, Duration.ofMinutes(1)));
            }

            return Bucket.builder().addLimit(limit).build();
        });
    }

    private String getClientIp(HttpServletRequest request) {
        // First check X-Real-IP (set by nginx)
        String ip = request.getHeader("X-Real-IP");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
            if (ip != null) {
                // X-Forwarded-For can contain multiple IPs, take the first one
                ip = ip.split(",")[0].trim();
            }
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}