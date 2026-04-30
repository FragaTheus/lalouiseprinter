package br.com.matheusfragadev.lalouise.infra.security.details;

import br.com.matheusfragadev.lalouise.domain.base.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.base.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.base.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.base.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.base.credentials.vo.Password;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDetailsImplTest {

    @Test
    void shouldExposeUserDataAndAuthorities() {
        Credentials credentials = mock(Credentials.class);
        Email email = mock(Email.class);
        Nickname nickname = mock(Nickname.class);
        Password password = mock(Password.class);
        UUID id = UUID.randomUUID();

        when(credentials.getId()).thenReturn(id);
        when(credentials.getEmail()).thenReturn(email);
        when(credentials.getNickname()).thenReturn(nickname);
        when(credentials.getPassword()).thenReturn(password);
        when(credentials.getRole()).thenReturn(Role.ADMIN);
        when(credentials.isActive()).thenReturn(true);
        when(email.value()).thenReturn("admin@lalouise.com");
        when(nickname.value()).thenReturn("Admin User");
        when(password.value()).thenReturn("hashed-password");

        UserDetailsImpl userDetails = new UserDetailsImpl(credentials);

        assertEquals(id, userDetails.getId());
        assertEquals("Admin User", userDetails.getNickname());
        assertEquals("admin@lalouise.com", userDetails.getUsername());
        assertEquals("hashed-password", userDetails.getPassword());
        assertEquals("ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
        assertTrue(userDetails.isEnabled());
    }
}
