package br.com.matheusfragadev.lalouise.infra.security.details;

import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.repository.CredentialsRepository;
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
    private CredentialsRepository credentialsRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldLoadActiveUserByUsername() {
        Credentials credentials = mock(Credentials.class);
        Email email = mock(Email.class);

        when(credentialsRepository.findByEmail(new Email("admin@lalouise.com"))).thenReturn(Optional.of(credentials));
        when(credentials.isActive()).thenReturn(true);
        when(credentials.getEmail()).thenReturn(email);
        when(email.value()).thenReturn("admin@lalouise.com");

        var userDetails = userDetailsService.loadUserByUsername("admin@lalouise.com");

        assertEquals("admin@lalouise.com", userDetails.getUsername());
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        when(credentialsRepository.findByEmail(new Email("none@lalouise.com"))).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("none@lalouise.com"));
    }

    @Test
    void shouldThrowWhenUserIsInactive() {
        Credentials credentials = mock(Credentials.class);
        when(credentialsRepository.findByEmail(new Email("admin@lalouise.com"))).thenReturn(Optional.of(credentials));
        when(credentials.isActive()).thenReturn(false);

        assertThrows(DisableUserException.class,
                () -> userDetailsService.loadUserByUsername("admin@lalouise.com"));
    }
}
