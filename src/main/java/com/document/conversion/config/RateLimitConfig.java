package com.document.conversion.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    private final long bucketCapacity;
    private final long refillTokens;
    private final long refillDuration;

    public RateLimitConfig(
            @Value("${app.ratelimiting.bucket-capacity}") long bucketCapacity,
            @Value("${app.ratelimiting.refill-tokens}") long refillTokens,
            @Value("${app.ratelimiting.refill-duration-mins}") long refillDuration) {
        this.bucketCapacity = bucketCapacity;
        this.refillTokens = refillTokens;
        this.refillDuration = refillDuration;
    }

    @Bean
    public Map<String, Bucket> buckets() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Bandwidth limit() {
        return Bandwidth.builder()
                .capacity(bucketCapacity)
                .refillIntervally(refillTokens, Duration.ofMinutes(refillDuration))
                .build();
    }
}
