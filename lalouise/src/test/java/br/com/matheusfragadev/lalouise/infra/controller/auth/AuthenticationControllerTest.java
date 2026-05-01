package br.com.matheusfragadev.lalouise.infra.controller.auth;

import br.com.matheusfragadev.lalouise.application.auth.AuthenticationService;
import br.com.matheusfragadev.lalouise.application.auth.LoginResult;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void loginShouldReturnAuthorizationHeaderAndResponseBody() {
        UUID userId = UUID.randomUUID();
        UserDetailsImpl userDetails = org.mockito.Mockito.mock(UserDetailsImpl.class);

        doReturn(List.of(new SimpleGrantedAuthority("ADMIN"))).when(userDetails).getAuthorities();

        when(userDetails.getId()).thenReturn(userId);
        when(userDetails.getNickname()).thenReturn("Admin User");
        when(userDetails.getUsername()).thenReturn("admin@lalouise.comabcde");
        when(authenticationService.authenticate("admin@lalouise.comabcde", "Strong@123"))
                .thenReturn(new LoginResult("jwt-token", userDetails));

        ResponseEntity<LoginResponse> response = authenticationController.login(
                new LoginRequest("admin@lalouise.comabcde", "Strong@123")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bearer jwt-token", response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        assertEquals(userId, response.getBody().id());
        assertEquals("Admin User", response.getBody().nickname());
        assertEquals("admin@lalouise.comabcde", response.getBody().email());
        assertEquals("ADMIN", response.getBody().role());
    }
}
