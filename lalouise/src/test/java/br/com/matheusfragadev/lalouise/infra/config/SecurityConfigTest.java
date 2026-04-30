package br.com.matheusfragadev.lalouise.infra.config;

import br.com.matheusfragadev.lalouise.infra.security.jwt.JwtFilter;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig(mock(JwtFilter.class));

    @Test
    void passwordEncoderShouldEncodeAndMatch() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        String encoded = encoder.encode("Strong@123");

        assertNotNull(encoded);
        assertTrue(encoder.matches("Strong@123", encoded));
    }

    @Test
    void corsConfigurationSourceShouldExposeAuthorizationHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");

        CorsConfiguration configuration = securityConfig.corsConfigurationSource().getCorsConfiguration(request);

        assertNotNull(configuration);
        assertEquals("http://localhost:3000", configuration.getAllowedOrigins().getFirst());
        assertTrue(configuration.getAllowedMethods().contains("POST"));
        assertEquals("*", configuration.getAllowedHeaders().getFirst());
        assertTrue(configuration.getExposedHeaders().contains("Authorization"));
    }

    @Test
    void authenticationManagerShouldComeFromAuthenticationConfiguration() throws Exception {
        AuthenticationConfiguration authenticationConfiguration = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(manager);

        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        assertEquals(manager, result);
    }
}

