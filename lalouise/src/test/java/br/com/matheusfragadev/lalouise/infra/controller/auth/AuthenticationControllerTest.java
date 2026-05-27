package br.com.matheusfragadev.lalouise.infra.controller.auth;

import br.com.matheusfragadev.lalouise.application.auth.AuthenticationService;
import br.com.matheusfragadev.lalouise.application.auth.LoginResult;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.LoginRequest;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.LoginResponse;
import br.com.matheusfragadev.lalouise.infra.security.bruteforce.BruteForceProtection;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private BruteForceProtection bruteForceProtection;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void loginShouldReturnAuthorizationHeaderAndResponseBody() {
        UUID userId = UUID.randomUUID();

        Admin admin = mock(Admin.class);
        Nickname nickname = mock(Nickname.class);
        Email email = mock(Email.class);

        when(admin.getId()).thenReturn(userId);
        when(admin.getNickname()).thenReturn(nickname);
        when(nickname.value()).thenReturn("Admin User");
        when(admin.getEmail()).thenReturn(email);
        when(email.value()).thenReturn("admin@lalouise.comabcde");
        when(admin.getRole()).thenReturn(Role.ADMIN);

        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

        when(userDetails.getCredentials()).thenReturn(admin);

        when(bruteForceProtection.isBlocked("admin@lalouise.comabcde"))
                .thenReturn(false);

        when(authenticationService.authenticate(
                "admin@lalouise.comabcde",
                "Strong@123"
        )).thenReturn(new LoginResult("jwt-token", userDetails));

        ResponseEntity<LoginResponse> response = authenticationController.login(
                new LoginRequest("admin@lalouise.comabcde", "Strong@123")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
                "Bearer jwt-token",
                response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)
        );

        LoginResponse body = response.getBody();

        assertEquals(userId.toString(), body.id());
        assertEquals("Admin User", body.nickname());
        assertEquals("admin@lalouise.comabcde", body.email());
        assertEquals("ADMIN", body.role());

        verify(bruteForceProtection)
                .resetAttempts("admin@lalouise.comabcde");

        verify(bruteForceProtection, never())
                .recordFailedAttempt(anyString());
    }
}