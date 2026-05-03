package br.com.matheusfragadev.lalouise.domain.credentials.vo;

import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordTest {

    @Test
    void shouldCreatePasswordUsingHasher() {
        Password password = Password.of("Strong@123", raw -> "HASH_" + raw);

        assertEquals("HASH_Strong@123", password.getValue());
    }

    @Test
    void shouldThrowWhenPasswordIsInvalid() {
        assertThrows(PasswordException.class, () -> Password.of("weak", raw -> raw));
    }

    @Test
    void shouldComparePasswordUsingMatcher() {
        Password password = Password.of("Strong@123", raw -> "HASH_" + raw);

        boolean samePassword = password.matches("Strong@123", (raw, encoded) -> encoded.equals("HASH_" + raw));
        boolean differentPassword = password.matches("Wrong@123", (raw, encoded) -> encoded.equals("HASH_" + raw));

        assertTrue(samePassword);
        assertFalse(differentPassword);
    }
}

