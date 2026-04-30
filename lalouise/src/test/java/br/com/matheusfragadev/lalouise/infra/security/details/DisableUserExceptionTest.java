package br.com.matheusfragadev.lalouise.infra.security.details;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DisableUserExceptionTest {

    @Test
    void shouldKeepExceptionMessage() {
        DisableUserException exception = new DisableUserException("inativo");

        assertEquals("inativo", exception.getMessage());
    }
}

