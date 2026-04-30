package br.com.matheusfragadev.lalouise.infra.controller.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginRequestTest {

    @Test
    void shouldExposeRecordFields() {
        LoginRequest request = new LoginRequest("admin@lalouise.comabcde", "Strong@123");

        assertEquals("admin@lalouise.comabcde", request.email());
        assertEquals("Strong@123", request.password());
    }
}

