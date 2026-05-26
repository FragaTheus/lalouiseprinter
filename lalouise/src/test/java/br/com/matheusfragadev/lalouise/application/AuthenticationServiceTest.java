package br.com.matheusfragadev.lalouise.application;
import br.com.matheusfragadev.lalouise.application.auth.AuthenticationService;
import br.com.matheusfragadev.lalouise.application.auth.LoginResult;
import br.com.matheusfragadev.lalouise.application.mail.EmailService;
import br.com.matheusfragadev.lalouise.application.user.profile.registry.UserServiceRegistry;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthenticationService authenticationService;
    @Mock
    private EmailService emailService;
    @Mock
    private UserServiceRegistry userServiceRegistry;

    @Test
    void authenticateShouldReturnTokenAndUserDetailsWhenCredentialsAreValid() {
        String email = "admin@lalouise.comabcde";
        String password = "Strong@123";
        String token = "token-value";
        UUID id = UUID.randomUUID();

        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(id);
        when(userDetails.getRole()).thenReturn(Role.ADMIN);
        when(userDetails.getUsername()).thenReturn(email);
        when(jwtService.generateToken(id.toString(), "ADMIN", null, null)).thenReturn(token);

        LoginResult result = authenticationService.authenticate(email, password);

        assertEquals(token, result.token());
        assertSame(userDetails, result.userDetails());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(emailService).sendSimpleEmail(any());
    }

    @Test
    void authenticateShouldThrowTranslatedMessageWhenAuthenticationFails() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));
        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authenticationService.authenticate("admin@lalouise.comabcde", "wrong"));
        assertEquals("Credenciais inválidas", exception.getMessage());
    }
}
