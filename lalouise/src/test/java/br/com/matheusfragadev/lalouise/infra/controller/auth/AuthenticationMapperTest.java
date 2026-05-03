package br.com.matheusfragadev.lalouise.infra.controller.auth;

import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.LoginResponse;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.mapper.AuthenticationMapper;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationMapperTest {

    @Test
    void toLoginResponseShouldMapAllFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getId()).thenReturn(id);
        when(userDetails.getNickname()).thenReturn("admin");
        when(userDetails.getUsername()).thenReturn("admin@teste.com.br");
        when(userDetails.getRole()).thenReturn(Role.ADMIN);

        LoginResponse response = AuthenticationMapper.toLoginResponse(userDetails);

        assertEquals(id, response.id());
        assertEquals("admin", response.nickname());
        assertEquals("admin@teste.com.br", response.email());
        assertEquals("ADMIN", response.role());
    }

    @Test
    void toLoginResponseShouldReturnLoginResponseInstance() {
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getId()).thenReturn(UUID.randomUUID());
        when(userDetails.getNickname()).thenReturn("admin");
        when(userDetails.getUsername()).thenReturn("admin@teste.com.br");
        when(userDetails.getRole()).thenReturn(Role.ADMIN);

        assertInstanceOf(LoginResponse.class, AuthenticationMapper.toLoginResponse(userDetails));
    }
}

