package br.com.matheusfragadev.lalouise.infra.controller.profile;
import br.com.matheusfragadev.lalouise.application.user.ManagerService;
import br.com.matheusfragadev.lalouise.application.user.StaffService;
import br.com.matheusfragadev.lalouise.application.user.profile.facade.ProfileFacade;
import br.com.matheusfragadev.lalouise.application.user.profile.utils.ProfileChangePassword;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.ProfileController;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.request.ChangeNameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.request.ChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response.AdminResponse;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response.ManagerResponse;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response.ProfileResponse;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.mapper.ProfileMapper;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {
    @Mock private ProfileFacade profileFacade;
    @Mock private ManagerService managerService;
    @Mock private StaffService staffService;
    @InjectMocks private ProfileController profileController;
    private UserDetailsImpl mockPrincipal(UUID id, Role role) {
        UserDetailsImpl principal = mock(UserDetailsImpl.class);
        when(principal.getId()).thenReturn(id);
        when(principal.getRole()).thenReturn(role);
        return principal;
    }
    @Test
    void getProfileShouldReturnAdminResponse() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.parse("2026-05-15T10:15:30Z");
        UserDetailsImpl principal = mockPrincipal(id, Role.ADMIN);
        Admin admin = mock(Admin.class);
        Nickname nickname = mock(Nickname.class);
        Email email = mock(Email.class);
        when(profileFacade.getProfile(id, Role.ADMIN)).thenReturn(admin);
        when(admin.getRole()).thenReturn(Role.ADMIN);
        when(admin.getId()).thenReturn(id);
        when(admin.getNickname()).thenReturn(nickname);
        when(admin.getEmail()).thenReturn(email);
        when(admin.getCreatedAt()).thenReturn(now);
        when(nickname.value()).thenReturn("Admin User");
        when(email.value()).thenReturn("admin@lalouise.com");
        ResponseEntity<ProfileResponse> response = profileController.getProfile(principal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new AdminResponse(id, "Admin User", "admin@lalouise.com", now), response.getBody());
    }
    @Test
    void getProfileShouldReturnManagerResponse() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Instant now = Instant.parse("2026-05-15T10:15:30Z");
        UserDetailsImpl principal = mockPrincipal(id, Role.MANAGER);
        Manager manager = mock(Manager.class);
        Nickname nickname = mock(Nickname.class);
        Email email = mock(Email.class);
        when(profileFacade.getProfile(id, Role.MANAGER)).thenReturn(manager);
        when(manager.getRole()).thenReturn(Role.MANAGER);
        when(manager.getId()).thenReturn(id);
        when(manager.getNickname()).thenReturn(nickname);
        when(manager.getEmail()).thenReturn(email);
        when(manager.getRestaurantId()).thenReturn(restaurantId);
        when(manager.getCreatedAt()).thenReturn(now);
        when(managerService.getRestaurantName(restaurantId)).thenReturn("La Louise");
        when(nickname.value()).thenReturn("Manager User");
        when(email.value()).thenReturn("manager@lalouise.com");
        ResponseEntity<ProfileResponse> response = profileController.getProfile(principal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new ManagerResponse(id, "Manager User", "manager@lalouise.com", "La Louise", Role.MANAGER, now), response.getBody());
    }
    @Test
    void changeNameShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        UserDetailsImpl principal = mockPrincipal(id, Role.ADMIN);
        ChangeNameRequest request = new ChangeNameRequest("New Name");
        doNothing().when(profileFacade).changeName(id, Role.ADMIN, "New Name");
        ResponseEntity<Void> response = profileController.changeName(principal, request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(profileFacade).changeName(id, Role.ADMIN, "New Name");
    }
    @Test
    void changePasswordShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        UserDetailsImpl principal = mockPrincipal(id, Role.ADMIN);
        ChangePasswordRequest request = new ChangePasswordRequest("Current@1", "NewPass@1", "NewPass@1");
        ProfileChangePassword command = ProfileMapper.toChangePasswordCommand(id, request);
        doNothing().when(profileFacade).changePassword(command, Role.ADMIN);
        ResponseEntity<Void> response = profileController.changePassword(principal, request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(profileFacade).changePassword(command, Role.ADMIN);
    }
}
