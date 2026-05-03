package br.com.matheusfragadev.lalouise.infra.controller.profile;
import br.com.matheusfragadev.lalouise.application.profile.utils.ProfileChangePassword;
import br.com.matheusfragadev.lalouise.application.profile.facade.ProfileFacade;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.mapper.ProfileMapper;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.request.ChangeNameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.request.ChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.response.AdminResponse;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.response.ProfileResponse;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {
    @Mock
    private ProfileFacade profileFacade;
    @InjectMocks
    private ProfileController profileController;
    private UserDetailsImpl mockPrincipal(UUID id, Role role) {
        UserDetailsImpl principal = mock(UserDetailsImpl.class);
        when(principal.getId()).thenReturn(id);
        when(principal.getRole()).thenReturn(role);
        return principal;
    }
    // ── getProfile ────────────────────────────────────────────────────────────
    @Test
    void getProfileShouldReturn200WithMappedBody() {
        UUID id = UUID.randomUUID();
        UserDetailsImpl principal = mockPrincipal(id, Role.ADMIN);
        // bare mock — mapping is fully delegated to ProfileMapper (mocked statically)
        Credentials credentials = mock(Credentials.class);
        Instant now = Instant.now();
        AdminResponse expectedResponse = new AdminResponse(id, "Admin User", "admin@lalouise.com", "ADMIN", true, now, now);
        when(profileFacade.getProfile(id, Role.ADMIN)).thenReturn(credentials);
        try (MockedStatic<ProfileMapper> mapper = mockStatic(ProfileMapper.class)) {
            mapper.when(() -> ProfileMapper.toResponse(credentials)).thenReturn(expectedResponse);
            ResponseEntity<ProfileResponse> response = profileController.getProfile(principal);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedResponse, response.getBody());
            verify(profileFacade).getProfile(id, Role.ADMIN);
        }
    }
    @Test
    void getProfileShouldPropagateExceptionFromFacade() {
        UUID id = UUID.randomUUID();
        UserDetailsImpl principal = mockPrincipal(id, Role.ADMIN);
        when(profileFacade.getProfile(id, Role.ADMIN))
                .thenThrow(new IllegalArgumentException("unexpected failure"));
        try {
            profileController.getProfile(principal);
        } catch (IllegalArgumentException ex) {
            assertEquals("unexpected failure", ex.getMessage());
        }
    }
    // ── changeName ────────────────────────────────────────────────────────────
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
    void changeNameShouldPropagateExceptionFromFacade() {
        UUID id = UUID.randomUUID();
        UserDetailsImpl principal = mockPrincipal(id, Role.ADMIN);
        ChangeNameRequest request = new ChangeNameRequest("Name");
        doThrow(new IllegalArgumentException("unexpected failure"))
                .when(profileFacade).changeName(id, Role.ADMIN, "Name");
        try {
            profileController.changeName(principal, request);
        } catch (IllegalArgumentException ex) {
            assertEquals("unexpected failure", ex.getMessage());
        }
    }
    // ── changePassword ────────────────────────────────────────────────────────
    @Test
    void changePasswordShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        UserDetailsImpl principal = mockPrincipal(id, Role.ADMIN);
        ChangePasswordRequest request = new ChangePasswordRequest("Current@1", "NewPass@1", "NewPass@1");
        ProfileChangePassword command = ProfileChangePassword.builder()
                .userId(id).currentPassword("Current@1")
                .newPassword("NewPass@1").confirmNewPassword("NewPass@1").build();
        try (MockedStatic<ProfileMapper> mapper = mockStatic(ProfileMapper.class)) {
            mapper.when(() -> ProfileMapper.toChangePasswordCommand(id, request)).thenReturn(command);
            doNothing().when(profileFacade).changePassword(command, Role.ADMIN);
            ResponseEntity<Void> response = profileController.changePassword(principal, request);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
            verify(profileFacade).changePassword(command, Role.ADMIN);
        }
    }
    @Test
    void changePasswordShouldPropagateExceptionFromFacade() {
        UUID id = UUID.randomUUID();
        UserDetailsImpl principal = mockPrincipal(id, Role.ADMIN);
        ChangePasswordRequest request = new ChangePasswordRequest("Current@1", "NewPass@1", "NewPass@1");
        ProfileChangePassword command = ProfileChangePassword.builder()
                .userId(id).currentPassword("Current@1")
                .newPassword("NewPass@1").confirmNewPassword("NewPass@1").build();
        try (MockedStatic<ProfileMapper> mapper = mockStatic(ProfileMapper.class)) {
            mapper.when(() -> ProfileMapper.toChangePasswordCommand(id, request)).thenReturn(command);
            doThrow(new IllegalArgumentException("unexpected failure"))
                    .when(profileFacade).changePassword(any(), eq(Role.ADMIN));
            try {
                profileController.changePassword(principal, request);
            } catch (IllegalArgumentException ex) {
                assertEquals("unexpected failure", ex.getMessage());
            }
        }
    }
}
