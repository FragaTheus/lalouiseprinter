package br.com.matheusfragadev.lalouise.domain.base.credentials.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CredentialExceptionsTest {

    @Test
    void activeExceptionShouldKeepMessage() {
        assertEquals("msg", new ActiveException("msg").getMessage());
    }

    @Test
    void emailExceptionShouldKeepMessage() {
        assertEquals("msg", new EmailException("msg").getMessage());
    }

    @Test
    void nicknameExceptionShouldKeepMessage() {
        assertEquals("msg", new NicknameException("msg").getMessage());
    }

    @Test
    void passwordExceptionShouldKeepMessage() {
        assertEquals("msg", new PasswordException("msg").getMessage());
    }
}

