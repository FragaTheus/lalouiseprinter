package br.com.matheusfragadev.lalouise.infra.controller.profile;

import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.response.AdminResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProfileResponseDtoTest {

    @Test
    void adminResponseShouldExposeAllFields() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        AdminResponse response = new AdminResponse(id, "admin", "admin@test.com", "ADMIN", true, now, now);

        assertEquals(id, response.id());
        assertEquals("admin", response.nickname());
        assertEquals("admin@test.com", response.email());
        assertEquals("ADMIN", response.role());
        assertTrue(response.active());
        assertEquals(now, response.createdAt());
        assertEquals(now, response.updatedAt());
    }
}

