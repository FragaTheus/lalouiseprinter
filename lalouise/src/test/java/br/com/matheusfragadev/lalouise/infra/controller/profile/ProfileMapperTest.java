package br.com.matheusfragadev.lalouise.infra.controller.profile;
import br.com.matheusfragadev.lalouise.application.user.profile.utils.ProfileChangePassword;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Staff;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.request.ChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response.AdminResponse;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response.ManagerResponse;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response.ProfileResponse;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response.StaffResponse;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.mapper.ProfileMapper;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
class ProfileMapperTest {
    @Test
    void toResponseShouldReturnAdminResponseWhenRoleIsAdmin() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.parse("2026-05-15T10:15:30Z");
        Admin admin = mock(Admin.class);
        Nickname nickname = mock(Nickname.class);
        Email email = mock(Email.class);
        when(admin.getRole()).thenReturn(Role.ADMIN);
        when(admin.getId()).thenReturn(id);
        when(admin.getNickname()).thenReturn(nickname);
        when(admin.getEmail()).thenReturn(email);
        when(admin.getCreatedAt()).thenReturn(now);
        when(nickname.value()).thenReturn("Admin User");
        when(email.value()).thenReturn("admin@lalouise.com");
        ProfileResponse response = ProfileMapper.toResponse(admin, idIgnored -> "", idIgnored -> "", idIgnored -> "");
        assertInstanceOf(AdminResponse.class, response);
        AdminResponse adminResponse = (AdminResponse) response;
        assertEquals(id, adminResponse.id());
        assertEquals("Admin User", adminResponse.nickname());
        assertEquals("admin@lalouise.com", adminResponse.email());
        assertEquals(now, adminResponse.createdAt());
    }
    @Test
    void toResponseShouldReturnManagerResponseWhenRoleIsManager() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Instant now = Instant.parse("2026-05-15T10:15:30Z");
        Manager manager = mock(Manager.class);
        Nickname nickname = mock(Nickname.class);
        Email email = mock(Email.class);
        when(manager.getRole()).thenReturn(Role.MANAGER);
        when(manager.getId()).thenReturn(id);
        when(manager.getNickname()).thenReturn(nickname);
        when(manager.getEmail()).thenReturn(email);
        when(manager.getRestaurantId()).thenReturn(restaurantId);
        when(manager.getCreatedAt()).thenReturn(now);
        when(nickname.value()).thenReturn("Manager User");
        when(email.value()).thenReturn("manager@lalouise.com");
        ProfileResponse response = ProfileMapper.toResponse(manager, idIgnored -> "La Louise", idIgnored -> "", idIgnored -> "");
        assertInstanceOf(ManagerResponse.class, response);
        ManagerResponse managerResponse = (ManagerResponse) response;
        assertEquals(id, managerResponse.id());
        assertEquals("Manager User", managerResponse.nickname());
        assertEquals("manager@lalouise.com", managerResponse.email());
        assertEquals("La Louise", managerResponse.restaurantName());
        assertEquals(Role.MANAGER, managerResponse.role());
        assertEquals(now, managerResponse.createdAt());
    }
    @Test
    void toResponseShouldReturnStaffResponseWhenRoleIsStaff() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID sectorId = UUID.randomUUID();
        Instant now = Instant.parse("2026-05-15T10:15:30Z");
        Staff staff = mock(Staff.class);
        Nickname nickname = mock(Nickname.class);
        Email email = mock(Email.class);
        when(staff.getRole()).thenReturn(Role.STAFF);
        when(staff.getId()).thenReturn(id);
        when(staff.getNickname()).thenReturn(nickname);
        when(staff.getEmail()).thenReturn(email);
        when(staff.getRestaurantId()).thenReturn(restaurantId);
        when(staff.getSectorId()).thenReturn(sectorId);
        when(staff.getCreatedAt()).thenReturn(now);
        when(nickname.value()).thenReturn("Staff User");
        when(email.value()).thenReturn("staff@lalouise.com");
        ProfileResponse response = ProfileMapper.toResponse(
                staff,
                idIgnored -> "La Louise",
                idIgnored -> "Cozinha",
                idIgnored -> "La Louise"
        );
        assertInstanceOf(StaffResponse.class, response);
        StaffResponse staffResponse = (StaffResponse) response;
        assertEquals(id, staffResponse.id());
        assertEquals("Staff User", staffResponse.nickname());
        assertEquals("staff@lalouise.com", staffResponse.email());
        assertEquals("La Louise", staffResponse.restaurantName());
        assertEquals("Cozinha", staffResponse.sectorName());
        assertEquals(Role.STAFF, staffResponse.role());
        assertEquals(now, staffResponse.createdAt());
    }
    @Test
    void toChangePasswordCommandShouldMapAllFieldsCorrectly() {
        UUID userId = UUID.randomUUID();
        ChangePasswordRequest request = new ChangePasswordRequest("Current@1", "NewPass@1", "NewPass@1");
        ProfileChangePassword command = ProfileMapper.toChangePasswordCommand(userId, request);
        assertEquals(userId, command.userId());
        assertEquals("Current@1", command.currentPassword());
        assertEquals("NewPass@1", command.newPassword());
        assertEquals("NewPass@1", command.confirmNewPassword());
    }
}
