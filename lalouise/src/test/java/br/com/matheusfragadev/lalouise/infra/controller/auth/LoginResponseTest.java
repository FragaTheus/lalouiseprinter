package br.com.matheusfragadev.lalouise.infra.controller.auth;

import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.AdminLoginResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginResponseTest {

    @Test
    void shouldExposeRecordFields() {
        UUID id = UUID.randomUUID();
        AdminLoginResponse response = new AdminLoginResponse(id.toString(), "Admin", "admin@lalouise.comabcde", "ADMIN");

        assertEquals(id.toString(), response.id());
        assertEquals("Admin", response.nickname());
        assertEquals("admin@lalouise.comabcde", response.email());
        assertEquals("ADMIN", response.role());
    }
}

