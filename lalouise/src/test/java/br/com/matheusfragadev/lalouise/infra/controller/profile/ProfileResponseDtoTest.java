package br.com.matheusfragadev.lalouise.infra.controller.profile;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.response.AdminResponse;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.response.ManagerResponse;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class ProfileResponseDtoTest {
    @Test
    void adminResponseShouldExposeAllFields() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        AdminResponse response = new AdminResponse(id, "admin", "admin@test.com", now);
        assertEquals(id, response.id());
        assertEquals("admin", response.nickname());
        assertEquals("admin@test.com", response.email());
        assertEquals(now, response.createdAt());
    }

    @Test
    void managerResponseShouldExposeAllFields() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        ManagerResponse response = new ManagerResponse(
                id,
                "manager",
                "manager@test.com",
                "La Louise",
                Role.MANAGER,
                now
        );

        assertEquals(id, response.id());
        assertEquals("manager", response.nickname());
        assertEquals("manager@test.com", response.email());
        assertEquals("La Louise", response.restaurantName());
        assertEquals(Role.MANAGER, response.role());
        assertEquals(now, response.createdAt());
    }
}
