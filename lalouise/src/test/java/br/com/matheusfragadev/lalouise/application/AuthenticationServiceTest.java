package br.com.matheusfragadev.lalouise.application;

import br.com.matheusfragadev.lalouise.application.auth.AccountLockedException;
import br.com.matheusfragadev.lalouise.application.auth.AuthenticationService;
import br.com.matheusfragadev.lalouise.application.auth.LoginResult;
import br.com.matheusfragadev.lalouise.application.mail.EmailService;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.infra.security.bruteforce.BruteForceProtection;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import br.com.matheusfragadev.lalouise.infra.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private EmailService emailService;
    @Mock private BruteForceProtection bruteForceProtection;
    @InjectMocks private AuthenticationService authenticationService;

    @Test
    void authenticateShouldReturnTokenAndUserDetailsWhenCredentialsAreValid() {
        String email = "admin@lalouise.com";
        String password = "Strong@123";
        String token = "token-value";
        UUID id = UUID.randomUUID();

        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        Credentials credentials = mock(Credentials.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getCredentials()).thenReturn(credentials);
        when(credentials.isNonLocked()).thenReturn(true);
        when(userDetails.getId()).thenReturn(id);
        when(userDetails.getRole()).thenReturn(Role.ADMIN);
        when(userDetails.getUsername()).thenReturn(email);
        when(jwtService.generateToken(id.toString(), "ADMIN", null, null)).thenReturn(token);

        LoginResult result = authenticationService.authenticate(email, password);

        assertEquals(token, result.token());
        assertSame(userDetails, result.userDetails());
        verify(bruteForceProtection).resetAttempts(email);
        verify(emailService).sendSimpleEmail(any());
    }

    @Test
    void authenticateShouldThrowAccountLockedExceptionWhenAccountIsLocked() {
        String email = "admin@lalouise.com";
        String password = "Strong@123";

        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        Credentials credentials = mock(Credentials.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getCredentials()).thenReturn(credentials);
        when(credentials.isNonLocked()).thenReturn(false);

        assertThrows(AccountLockedException.class,
                () -> authenticationService.authenticate(email, password));
    }

    @Test
    void authenticateShouldThrowTranslatedMessageAndRecordAttemptWhenAuthenticationFails() {
        String email = "admin@lalouise.com";
        String password = "wrong";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authenticationService.authenticate(email, password));

        assertEquals("Credenciais inválidas", exception.getMessage());
        verify(bruteForceProtection).recordFailedAttempt(email);
    }
}
