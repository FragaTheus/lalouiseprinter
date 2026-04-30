package br.com.matheusfragadev.lalouise.application;

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
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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

    @Test
    void authenticateShouldReturnTokenAndUserDetailsWhenCredentialsAreValid() {
        String email = "admin@lalouise.comabcde";
        String password = "Strong@123";
        String token = "token-value";
        UUID id = UUID.randomUUID();

        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        UserDetailsImpl userDetails = org.mockito.Mockito.mock(UserDetailsImpl.class);

        doReturn(List.of(new SimpleGrantedAuthority("ADMIN"))).when(userDetails).getAuthorities();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(id);
        when(jwtService.generateToken(id.toString(), "ADMIN")).thenReturn(token);

        LoginResult result = authenticationService.authenticate(email, password);

        assertEquals(token, result.token());
        assertSame(userDetails, result.userDetails());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
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
