package br.com.matheusfragadev.lalouise.infra.controller.profile;
import br.com.matheusfragadev.lalouise.application.user.profile.utils.ProfileChangePassword;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.mapper.ProfileMapper;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.request.ChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response.AdminResponse;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response.ManagerResponse;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response.ProfileResponse;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
class ProfileMapperTest {
    // ── toResponse ────────────────────────────────────────────────────────────
    @Test
    void toResponseShouldReturnAdminResponseWhenRoleIsAdmin() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Nickname nickname = mock(Nickname.class);
        Email email = mock(Email.class);
        when(admin.getRole()).thenReturn(Role.ADMIN);
        when(admin.getId()).thenReturn(id);
        when(admin.getNickname()).thenReturn(nickname);
        when(admin.getEmail()).thenReturn(email);
        when(nickname.value()).thenReturn("Admin User");
        when(email.value()).thenReturn("admin@lalouise.com");
        ProfileResponse response = ProfileMapper.toResponse(admin, idIgnored -> "");
        assertInstanceOf(AdminResponse.class, response);
        AdminResponse adminResponse = (AdminResponse) response;
        assertEquals(id, adminResponse.id());
        assertEquals("Admin User", adminResponse.nickname());
        assertEquals("admin@lalouise.com", adminResponse.email());
    }

    // ── toChangePasswordCommand ───────────────────────────────────────────────
    @Test
    void toChangePasswordCommandShouldMapAllFieldsCorrectly() {
        UUID userId = UUID.randomUUID();
        ChangePasswordRequest request = new ChangePasswordRequest(
                "Current@1", "NewPass@1", "NewPass@1"
        );
        ProfileChangePassword command = ProfileMapper.toChangePasswordCommand(userId, request);
        assertEquals(userId, command.userId());
        assertEquals("Current@1", command.currentPassword());
        assertEquals("NewPass@1", command.newPassword());
        assertEquals("NewPass@1", command.confirmNewPassword());
    }

    // ── toResponse — MANAGER ──────────────────────────────────────────────────
    @Test
    void toResponseShouldReturnManagerResponseWhenRoleIsManager() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        Nickname nickname = mock(Nickname.class);
        Email email = mock(Email.class);

        when(manager.getRole()).thenReturn(Role.MANAGER);
        when(manager.getId()).thenReturn(id);
        when(manager.getNickname()).thenReturn(nickname);
        when(manager.getEmail()).thenReturn(email);
        when(manager.getRestaurantId()).thenReturn(restaurantId);
        when(nickname.value()).thenReturn("Manager User");
        when(email.value()).thenReturn("manager@lalouise.com");

        ProfileResponse response = ProfileMapper.toResponse(manager, idIgnored -> "La Louise");

        assertInstanceOf(ManagerResponse.class, response);
        ManagerResponse managerResponse = (ManagerResponse) response;
        assertEquals(id, managerResponse.id());
        assertEquals("Manager User", managerResponse.nickname());
        assertEquals("manager@lalouise.com", managerResponse.email());
        assertEquals("La Louise", managerResponse.restaurantName());
        assertEquals(Role.MANAGER, managerResponse.role());
    }
}
