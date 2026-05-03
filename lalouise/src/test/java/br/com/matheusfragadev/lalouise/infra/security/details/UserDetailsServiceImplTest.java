package br.com.matheusfragadev.lalouise.infra.security.details;

import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.admin.repository.AdminRepository;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private AdminRepository adminRepository;


    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldLoadActiveUserByUsername() {
        Admin admin = mock(Admin.class);
        Email email = mock(Email.class);

        when(adminRepository.findByEmail(new Email("admin@lalouise.com"))).thenReturn(Optional.of(admin));
        when(admin.isActive()).thenReturn(true);
        when(admin.getEmail()).thenReturn(email);
        when(email.value()).thenReturn("admin@lalouise.com");

        var userDetails = userDetailsService.loadUserByUsername("admin@lalouise.com");

        assertEquals("admin@lalouise.com", userDetails.getUsername());
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        when(adminRepository.findByEmail(new Email("none@lalouise.com"))).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("none@lalouise.com"));
    }

    @Test
    void shouldThrowWhenUserIsInactive() {
        Admin admin = mock(Admin.class);
        when(adminRepository.findByEmail( new Email("admin@lalouise.com"))).thenReturn(Optional.of(admin));
        when(admin.isActive()).thenReturn(false);

        assertThrows(DisableUserException.class,
                () -> userDetailsService.loadUserByUsername("admin@lalouise.com"));
    }
}
