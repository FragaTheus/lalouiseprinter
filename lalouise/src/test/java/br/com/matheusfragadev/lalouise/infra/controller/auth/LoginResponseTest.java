package br.com.matheusfragadev.lalouise.infra.controller.auth;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginResponseTest {

    @Test
    void shouldExposeRecordFields() {
        UUID id = UUID.randomUUID();
        LoginResponse response = new LoginResponse(id, "Admin", "admin@lalouise.comabcde", "ADMIN");

        assertEquals(id, response.id());
        assertEquals("Admin", response.nickname());
        assertEquals("admin@lalouise.comabcde", response.email());
        assertEquals("ADMIN", response.role());
    }
}

