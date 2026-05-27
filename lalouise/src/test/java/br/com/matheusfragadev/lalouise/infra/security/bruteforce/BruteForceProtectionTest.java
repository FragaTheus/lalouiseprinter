package br.com.matheusfragadev.lalouise.infra.security.bruteforce;

import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.repository.CredentialsRepository;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BruteForceProtectionTest {

    @Mock
    private CredentialsRepository credentialsRepository;

    @InjectMocks
    private BruteForceProtection bruteForceProtection;

    private final String email = "user@lalouise.com";

    @BeforeEach
    void setUp() {
        // Limpa estado entre testes (re-instanciado via @InjectMocks a cada teste)
    }

    @Test
    void recordFailedAttemptShouldIncrementCount() {
        bruteForceProtection.recordFailedAttempt(email);

        assertEquals(1, bruteForceProtection.getMaxAttemptsCount(email));
    }

    @Test
    void recordFailedAttemptShouldLockUserWhenMaxAttemptsReached() {
        Credentials credentials = mock(Credentials.class);
        when(credentialsRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(credentials));

        // Chega até MAX_ATTEMPTS = 5
        for (int i = 0; i < 5; i++) {
            bruteForceProtection.recordFailedAttempt(email);
        }

        verify(credentials).lockFor(any());
        verify(credentialsRepository).save(credentials);
    }

    @Test
    void recordFailedAttemptShouldNotLockWhenBelowMaxAttempts() {
        // 4 tentativas — não deve bloquear
        for (int i = 0; i < 4; i++) {
            bruteForceProtection.recordFailedAttempt(email);
        }

        assertEquals(4, bruteForceProtection.getMaxAttemptsCount(email));
        verify(credentialsRepository, never()).save(any());
    }

    @Test
    void recordFailedAttemptShouldNotThrowWhenUserNotFoundInDatabase() {
        when(credentialsRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        for (int i = 0; i < 5; i++) {
            bruteForceProtection.recordFailedAttempt(email);
        }

        verify(credentialsRepository, never()).save(any());
    }

    @Test
    void resetAttemptsShouldUnlockUserAndClearCount() {
        Credentials credentials = mock(Credentials.class);
        when(credentialsRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(credentials));

        // Registra algumas tentativas
        bruteForceProtection.recordFailedAttempt(email);
        bruteForceProtection.recordFailedAttempt(email);

        bruteForceProtection.resetAttempts(email);

        assertEquals(0, bruteForceProtection.getMaxAttemptsCount(email));
        verify(credentials).unlock();
        verify(credentialsRepository).save(credentials);
    }

    @Test
    void resetAttemptsShouldNotThrowWhenUserNotFoundInDatabase() {
        when(credentialsRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        bruteForceProtection.resetAttempts(email); // não deve lançar exceção

        verify(credentialsRepository, never()).save(any());
    }

    @Test
    void getMaxAttemptsCountShouldReturnZeroWhenNoAttemptsMade() {
        assertEquals(0, bruteForceProtection.getMaxAttemptsCount(email));
    }
}


