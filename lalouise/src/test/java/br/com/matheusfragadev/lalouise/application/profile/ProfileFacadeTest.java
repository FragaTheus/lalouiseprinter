package br.com.matheusfragadev.lalouise.application.profile;
import br.com.matheusfragadev.lalouise.application.profile.facade.ProfileFacade;
import br.com.matheusfragadev.lalouise.application.profile.registry.ProfileServiceRegistry;
import br.com.matheusfragadev.lalouise.application.profile.utils.ProfileChangePassword;
import br.com.matheusfragadev.lalouise.domain.base.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.base.credentials.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProfileFacadeTest {
    @Mock
    private ProfileServiceRegistry profileServiceRegistry;
    @InjectMocks
    private ProfileFacade profileFacade;
    @SuppressWarnings("unchecked")
    private ProfileService<Credentials> mockServiceFor(Role role) {
        ProfileService<Credentials> service = mock(ProfileService.class);
        // doReturn bypasses wildcard type-check on ProfileService<? extends Credentials>
        doReturn(service).when(profileServiceRegistry).resolve(role);
        return service;
    }
    // ── getProfile ───────────────────────────────────────────────────────────
    @Test
    void getProfileShouldDelegateToResolvedServiceAndReturnCredentials() {
        UUID id = UUID.randomUUID();
        Credentials credentials = mock(Credentials.class);
        var service = mockServiceFor(Role.ADMIN);
        when(service.getProfile(id)).thenReturn(credentials);
        var result = profileFacade.getProfile(id, Role.ADMIN);
        assertSame(credentials, result);
        verify(profileServiceRegistry).resolve(Role.ADMIN);
        verify(service).getProfile(id);
    }
    @Test
    void getProfileShouldPropagateExceptionWhenRoleNotRegistered() {
        when(profileServiceRegistry.resolve(Role.MANAGER))
                .thenThrow(new IllegalArgumentException("No UserService found for role: MANAGER"));
        assertThrows(IllegalArgumentException.class,
                () -> profileFacade.getProfile(UUID.randomUUID(), Role.MANAGER));
    }
    // ── changeName ───────────────────────────────────────────────────────────
    @Test
    void changeNameShouldDelegateToResolvedService() {
        UUID id = UUID.randomUUID();
        var service = mockServiceFor(Role.ADMIN);
        profileFacade.changeName(id, Role.ADMIN, "New Name");
        verify(profileServiceRegistry).resolve(Role.ADMIN);
        verify(service).changeName(id, "New Name");
    }
    @Test
    void changeNameShouldPropagateExceptionWhenRoleNotRegistered() {
        when(profileServiceRegistry.resolve(Role.MANAGER))
                .thenThrow(new IllegalArgumentException("No UserService found for role: MANAGER"));
        assertThrows(IllegalArgumentException.class,
                () -> profileFacade.changeName(UUID.randomUUID(), Role.MANAGER, "Name"));
    }
    // ── changePassword ───────────────────────────────────────────────────────
    @Test
    void changePasswordShouldDelegateToResolvedService() {
        UUID id = UUID.randomUUID();
        var command = ProfileChangePassword.builder()
                .userId(id)
                .currentPassword("Current@1")
                .newPassword("NewPass@1")
                .confirmNewPassword("NewPass@1")
                .build();
        var service = mockServiceFor(Role.ADMIN);
        profileFacade.changePassword(command, Role.ADMIN);
        verify(profileServiceRegistry).resolve(Role.ADMIN);
        verify(service).changePassword(command);
    }
    @Test
    void changePasswordShouldPropagateExceptionWhenRoleNotRegistered() {
        var command = ProfileChangePassword.builder()
                .userId(UUID.randomUUID())
                .currentPassword("Current@1")
                .newPassword("NewPass@1")
                .confirmNewPassword("NewPass@1")
                .build();
        when(profileServiceRegistry.resolve(Role.MANAGER))
                .thenThrow(new IllegalArgumentException("No UserService found for role: MANAGER"));
        assertThrows(IllegalArgumentException.class,
                () -> profileFacade.changePassword(command, Role.MANAGER));
    }
}
