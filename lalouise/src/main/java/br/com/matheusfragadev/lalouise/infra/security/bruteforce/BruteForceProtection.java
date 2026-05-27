package br.com.matheusfragadev.lalouise.infra.security.bruteforce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@RequiredArgsConstructor
public class BruteForceProtection {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MINUTES = 15;

    private final ConcurrentHashMap<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    public void recordFailedAttempt(String email) {
        LoginAttempt attempt = loginAttempts.computeIfAbsent(email, k -> new LoginAttempt());

        Instant now = Instant.now();

        if (attempt.lastAttemptTime != null &&
                now.isAfter(attempt.lastAttemptTime.plusSeconds(LOCK_TIME_MINUTES * 60))) {
            attempt.failedCount.set(0);
            attempt.lockedUntil = null;
        }

        attempt.failedCount.incrementAndGet();
        attempt.lastAttemptTime = now;

        if (attempt.failedCount.get() >= MAX_ATTEMPTS) {
            attempt.lockedUntil = now.plusSeconds(LOCK_TIME_MINUTES * 60);
            log.warn("🚨 BRUTE FORCE - Email: {} bloqueado até {}", email, attempt.lockedUntil);
        }
    }

    public boolean isBlocked(String email) {
        LoginAttempt attempt = loginAttempts.get(email);

        if (attempt == null || attempt.lockedUntil == null) {
            return false;
        }

        Instant now = Instant.now();

        if (now.isAfter(attempt.lockedUntil)) {
            attempt.failedCount.set(0);
            attempt.lockedUntil = null;
            return false;
        }

        return true;
    }

    public void resetAttempts(String email) {
        LoginAttempt attempt = loginAttempts.get(email);
        if (attempt != null) {
            attempt.failedCount.set(0);
            attempt.lockedUntil = null;
            log.info("✅ Tentativas resetadas para: {}", email);
        }
    }

    public int getAttemptCount(String email) {
        LoginAttempt attempt = loginAttempts.get(email);
        if (attempt == null) return 0;

        Instant now = Instant.now();
        if (attempt.lastAttemptTime != null &&
                now.isAfter(attempt.lastAttemptTime.plusSeconds(LOCK_TIME_MINUTES * 60))) {
            return 0;
        }

        return attempt.failedCount.get();
    }

    public static class LoginAttempt {
        public AtomicInteger failedCount = new AtomicInteger(0);
        public Instant lastAttemptTime;
        public Instant lockedUntil;
    }
}
