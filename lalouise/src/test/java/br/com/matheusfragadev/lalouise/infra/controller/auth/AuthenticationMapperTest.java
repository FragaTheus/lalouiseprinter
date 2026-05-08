package br.com.matheusfragadev.lalouise.infra.controller.auth;

import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.AdminLoginResponse;
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
        Admin admin = mock(Admin.class);
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

        when(userDetails.getCredentials()).thenReturn(admin);
        when(admin.getId()).thenReturn(id);
        when(admin.getNickname()).thenReturn(mock(br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname.class));
        when(admin.getNickname().value()).thenReturn("admin");
        when(admin.getEmail()).thenReturn(mock(br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email.class));
        when(admin.getEmail().value()).thenReturn("admin@teste.com.br");
        when(admin.getRole()).thenReturn(br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role.ADMIN);

        LoginResponse response = AuthenticationMapper.toLoginResponse(userDetails);

        assertEquals(id.toString(), response.id());
        assertEquals("admin", response.nickname());
        assertEquals("admin@teste.com.br", response.email());
        assertEquals("ADMIN", response.role());
    }

    @Test
    void toLoginResponseShouldReturnAdminLoginResponseForAdmin() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

        when(userDetails.getCredentials()).thenReturn(admin);
        when(admin.getId()).thenReturn(id);
        when(admin.getNickname()).thenReturn(mock(br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname.class));
        when(admin.getNickname().value()).thenReturn("admin");
        when(admin.getEmail()).thenReturn(mock(br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email.class));
        when(admin.getEmail().value()).thenReturn("admin@teste.com.br");
        when(admin.getRole()).thenReturn(br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role.ADMIN);

        assertInstanceOf(AdminLoginResponse.class, AuthenticationMapper.toLoginResponse(userDetails));
    }
}
