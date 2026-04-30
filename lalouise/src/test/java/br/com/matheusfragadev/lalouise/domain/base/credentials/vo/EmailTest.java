package br.com.matheusfragadev.lalouise.domain.base.credentials.vo;

import br.com.matheusfragadev.lalouise.domain.base.credentials.exception.EmailException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    void shouldThrowWhenEmailIsBlank() {
        assertThrows(EmailException.class, () -> new Email("   "));
    }

    @Test
    void shouldThrowWhenEmailHasCommonValidFormatDueToCurrentRegexRule() {
        assertThrows(EmailException.class, () -> new Email("user@lalouise.com"));
    }

    @Test
    void shouldThrowWhenEmailIsInvalid() {
        assertThrows(EmailException.class, () -> new Email("invalid-email"));
    }
}
