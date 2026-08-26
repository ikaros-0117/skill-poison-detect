package com.skilldetect.server.config;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.skilldetect.server.common.BusinessException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Redis fixed-window rate limiter keyed by client IP and API group (HMI/M2M). */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redis;
    private final RateLimitProperties properties;

    public RateLimitInterceptor(StringRedisTemplate redis, RateLimitProperties properties) {
        this.redis = redis;
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!properties.isEnabled()) {
            return true;
        }
        String uri = request.getRequestURI();
        boolean m2m = uri.contains("/api/m2m/");
        int limit = m2m ? properties.getM2mRequestsPerMinute() : properties.getHmiRequestsPerMinute();
        String group = m2m ? "m2m" : "hmi";
        String ip = clientIp(request);
        long window = System.currentTimeMillis() / 1000 / properties.getWindowSeconds();
        String key = "ratelimit:" + group + ":" + ip + ":" + window;

        Long count = redis.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redis.expire(key, Duration.ofSeconds(properties.getWindowSeconds()));
        }
        if (count != null && count > limit) {
            throw new BusinessException(HttpStatus.TOO_MANY_REQUESTS, 42900, "请求过于频繁，请稍后再试");
        }
        return true;
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            return (comma > 0 ? forwarded.substring(0, comma) : forwarded).trim();
        }
        return request.getRemoteAddr();
    }
}
