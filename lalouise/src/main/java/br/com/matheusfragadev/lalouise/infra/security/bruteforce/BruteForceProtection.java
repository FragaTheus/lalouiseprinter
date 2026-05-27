package br.com.matheusfragadev.lalouise.infra.security.bruteforce;

import br.com.matheusfragadev.lalouise.domain.user.credentials.repository.CredentialsRepository;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@RequiredArgsConstructor
public class BruteForceProtection {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MINUTES = 15;

    private final CredentialsRepository credentialsRepository;

    private final ConcurrentHashMap<String, AtomicInteger> loginAttempts = new ConcurrentHashMap<>();

    public void recordFailedAttempt(String email) {
        int failed = loginAttempts.computeIfAbsent
                (email, k -> new AtomicInteger(0)).incrementAndGet();

        if (failed >= MAX_ATTEMPTS){
            var credentials = credentialsRepository.findByEmail(new Email(email))
                    .orElse(null);
            if (credentials != null){
                credentials.lockFor(Duration.ofMinutes(LOCK_TIME_MINUTES));
                credentialsRepository.save(credentials);
                log.warn("🚨 BRUTE FORCE - Email: {} bloqueado até {}", email, credentials.getLockedUntil());            }
        }

    }

    public void resetAttempts(String email) {
        loginAttempts.remove(email);
        var credentials = credentialsRepository.findByEmail(new Email(email))
                .orElse(null);
        if (credentials != null) {
            credentials.unlock();
            credentialsRepository.save(credentials);
            log.info("✅ Tentativas resetadas e conta desbloqueada para: {}", email);        }
    }

    public int getMaxAttemptsCount(String email){
        return loginAttempts.getOrDefault(email, new AtomicInteger(0)).get();
    }

}
