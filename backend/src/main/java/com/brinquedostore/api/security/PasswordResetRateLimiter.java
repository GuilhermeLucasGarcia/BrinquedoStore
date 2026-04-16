package com.brinquedostore.api.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class PasswordResetRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MILLIS = Duration.ofMinutes(15).toMillis();
    private final ConcurrentHashMap<String, Deque<Long>> attemptsByKey = new ConcurrentHashMap<>();

    public boolean tryAcquire(String key) {
        long now = System.currentTimeMillis();
        Deque<Long> attempts = attemptsByKey.computeIfAbsent(key, ignored -> new ConcurrentLinkedDeque<>());

        synchronized (attempts) {
            while (!attempts.isEmpty() && now - attempts.peekFirst() > WINDOW_MILLIS) {
                attempts.pollFirst();
            }

            if (attempts.size() >= MAX_ATTEMPTS) {
                return false;
            }

            attempts.addLast(now);
            return true;
        }
    }
}
