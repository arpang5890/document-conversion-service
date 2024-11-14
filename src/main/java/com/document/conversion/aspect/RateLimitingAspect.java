package com.document.conversion.aspect;

import com.document.conversion.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitingAspect {

    private final Map<String, Bucket> buckets;
    private final Bandwidth limit;

    @Around("@annotation(com.document.conversion.annotation.RateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        String clientId = getClientId(request);
        Bucket bucket = buckets.computeIfAbsent(clientId,
                k -> Bucket.builder().addLimit(limit).build());

        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        } else {
            throw new RateLimitExceededException("Rate limit exceeded for client: " + clientId);
        }
    }

    private String getClientId(HttpServletRequest request) {
        // In a production environment, this should be replaced with a more robust
        // client identification mechanism (e.g., API key, JWT token)
        return request.getRemoteAddr();
    }
}
