package com.alone.nexus.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitConfig {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final Map<String, Instant> lastAccess = new ConcurrentHashMap<>();

    @Value("${nexus.rate-limit.events-per-minute:60}")
    private int eventsPerMinute;

    public Bucket resolveBucket(String agentName) {
        lastAccess.put(agentName, Instant.now());
        return buckets.computeIfAbsent(agentName, key -> newBucket());
    }

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.classic(eventsPerMinute,
                Refill.greedy(eventsPerMinute, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void cleanupInactiveBuckets() {
        Instant threshold = Instant.now().minus(Duration.ofMinutes(10));
        lastAccess.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().isBefore(threshold);
            if (expired) {
                buckets.remove(entry.getKey());
            }
            return expired;
        });
    }
}
