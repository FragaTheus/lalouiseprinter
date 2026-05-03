package br.com.matheusfragadev.lalouise.domain.credentials.vo;

import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.EmailException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    void shouldThrowWhenEmailIsBlank() {
        assertThrows(EmailException.class, () -> new Email("   "));
    }

    @Test
    void shouldNotThrowWhenEmailHasValidFormat() {
        assertDoesNotThrow(() -> new Email("user@lalouise.com"));
    }

    @Test
    void shouldThrowWhenEmailIsInvalid() {
        assertThrows(EmailException.class, () -> new Email("invalid-email"));
    }
}
