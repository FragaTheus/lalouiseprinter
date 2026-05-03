package br.com.matheusfragadev.lalouise.application.profile;
import br.com.matheusfragadev.lalouise.application.profile.registry.ProfileServiceRegistry;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
class ProfileServiceRegistryTest {
    @SuppressWarnings("unchecked")
    private ProfileService<? extends Credentials> mockService(Role role) {
        ProfileService<Credentials> service = mock(ProfileService.class);
        when(service.getRole()).thenReturn(role);
        return service;
    }
    @Test
    void shouldResolveCorrectServiceForRole() {
        var adminService = mockService(Role.ADMIN);
        var registry = new ProfileServiceRegistry(List.of(adminService));
        var resolved = registry.resolve(Role.ADMIN);
        assertSame(adminService, resolved);
    }
    @Test
    void shouldThrowWhenNoServiceRegisteredForRole() {
        var registry = new ProfileServiceRegistry(List.of());
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> registry.resolve(Role.ADMIN)
        );
        assertTrue(ex.getMessage().contains("ADMIN"));
    }
    @Test
    void shouldThrowWhenRegistryIsEmpty() {
        var registry = new ProfileServiceRegistry(List.of());
        assertThrows(IllegalArgumentException.class, () -> registry.resolve(Role.ADMIN));
    }
}
