package br.com.matheusfragadev.lalouise.infra.controller.auth;

import br.com.matheusfragadev.lalouise.application.auth.AuthenticationService;
import br.com.matheusfragadev.lalouise.application.auth.LoginResult;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.LoginRequest;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.LoginResponse;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private UserDetailsImpl userDetails;
    private final String email = "user@lalouise.com";
    private final String token = "jwt-token";

    @BeforeEach
    void setUp() throws Exception {
        Admin admin = new Admin(
                new Nickname("Test User"),
                new Email(email),
                Password.of("Strong@123", s -> "hashed_password")
        );
        // ID é gerado pelo JPA; forçamos via reflection para testes unitários
        var idField = br.com.matheusfragadev.lalouise.domain.auditory.Auditory.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(admin, UUID.randomUUID());

        userDetails = new UserDetailsImpl(admin);
    }

    @Test
    void loginShouldReturnResponseEntityWithTokenAndUserDetails() {
        var loginRequest = new LoginRequest(email, "Strong@123");
        var loginResult = new LoginResult(token, userDetails);

        when(authenticationService.authenticate(email, "Strong@123")).thenReturn(loginResult);

        ResponseEntity<LoginResponse> response = authenticationController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bearer " + token, response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        assertNotNull(response.getBody());
        verify(authenticationService).authenticate(email, "Strong@123");
    }
}