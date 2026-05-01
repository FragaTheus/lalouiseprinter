package br.com.matheusfragadev.lalouise.infra.controller.profile;
import br.com.matheusfragadev.lalouise.application.profile.utils.ProfileChangePassword;
import br.com.matheusfragadev.lalouise.domain.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.base.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.base.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.base.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.base.credentials.vo.Nickname;
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
        when(admin.isActive()).thenReturn(true);
        when(nickname.value()).thenReturn("Admin User");
        when(email.value()).thenReturn("admin@lalouise.com");
        ProfileResponse response = ProfileMapper.toResponse(admin);
        assertInstanceOf(AdminResponse.class, response);
        AdminResponse adminResponse = (AdminResponse) response;
        assertEquals(id, adminResponse.id());
        assertEquals("Admin User", adminResponse.nickname());
        assertEquals("admin@lalouise.com", adminResponse.email());
        assertEquals("ADMIN", adminResponse.role());
        assertTrue(adminResponse.active());
    }
    @Test
    void toResponseShouldReturnStaffResponseWhenRoleIsStaff() {
        UUID id = UUID.randomUUID();
        Credentials credentials = mock(Credentials.class);
        Nickname nickname = mock(Nickname.class);
        Email email = mock(Email.class);
        when(credentials.getRole()).thenReturn(Role.STAFF);
        when(credentials.getId()).thenReturn(id);
        when(credentials.getNickname()).thenReturn(nickname);
        when(credentials.getEmail()).thenReturn(email);
        when(credentials.isActive()).thenReturn(false);
        when(nickname.value()).thenReturn("Staff User");
        when(email.value()).thenReturn("staff@lalouise.com");
        ProfileResponse response = ProfileMapper.toResponse(credentials);
        assertInstanceOf(StaffResponse.class, response);
        StaffResponse staffResponse = (StaffResponse) response;
        assertEquals(id, staffResponse.id());
        assertEquals("Staff User", staffResponse.nickname());
        assertEquals("staff@lalouise.com", staffResponse.email());
        assertEquals("STAFF", staffResponse.role());
        assertFalse(staffResponse.active());
    }
    @Test
    void toResponseShouldThrowForUnmappedRole() {
        Credentials credentials = mock(Credentials.class);
        when(credentials.getRole()).thenReturn(Role.MANAGER);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> ProfileMapper.toResponse(credentials)
        );
        assertTrue(ex.getMessage().contains("MANAGER"));
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
}
