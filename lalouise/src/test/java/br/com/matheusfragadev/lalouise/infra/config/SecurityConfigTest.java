package br.com.matheusfragadev.lalouise.infra.config;

import br.com.matheusfragadev.lalouise.infra.entrypoint.CustomEntryPoint;
import br.com.matheusfragadev.lalouise.infra.security.jwt.JwtFilter;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig(mock(JwtFilter.class), mock(CustomEntryPoint.class));

    // ── passwordEncoder ────────────────────────────────────────────────────────

    @Test
    void passwordEncoderShouldEncodeAndMatch() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        String encoded = encoder.encode("Strong@123");

        assertNotNull(encoded);
        assertTrue(encoder.matches("Strong@123", encoded));
    }

    // ── corsConfigurationSource ───────────────────────────────────────────────

    @Test
    void corsConfigurationSourceShouldExposeAuthorizationHeader() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");

        CorsConfiguration config = source.getCorsConfiguration(request);

        assertNotNull(config);
        assertTrue(config.getAllowedMethods().contains("POST"));
        assertTrue(config.getAllowedMethods().contains("GET"));
        assertEquals("*", config.getAllowedHeaders().getFirst());
        assertTrue(config.getExposedHeaders().contains("Authorization"));
    }

    @Test
    void corsConfigurationSourceShouldAllowLocalhostOrigin() {
        CorsConfiguration config = securityConfig.corsConfigurationSource()
                .getCorsConfiguration(new MockHttpServletRequest());

        assertNotNull(config);
        // allowedOriginPatterns is used — not allowedOrigins
        assertTrue(config.getAllowedOriginPatterns().contains("http://localhost:3000"));
    }

    @Test
    void corsConfigurationSourceShouldAllowVercelProductionOrigin() {
        CorsConfiguration config = securityConfig.corsConfigurationSource()
                .getCorsConfiguration(new MockHttpServletRequest());

        assertNotNull(config);
        assertTrue(config.getAllowedOriginPatterns().contains("https://lalouiseprinter-upsj.vercel.app"));
    }

    @Test
    void corsConfigurationSourceShouldAllowVercelPreviewOriginPattern() {
        CorsConfiguration config = securityConfig.corsConfigurationSource()
                .getCorsConfiguration(new MockHttpServletRequest());

        assertNotNull(config);
        assertTrue(config.getAllowedOriginPatterns().contains("https://lalouiseprinter-upsj-*.vercel.app"));
    }

    // ── authenticationManager ─────────────────────────────────────────────────

    @Test
    void authenticationManagerShouldComeFromAuthenticationConfiguration() throws Exception {
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);
        when(authConfig.getAuthenticationManager()).thenReturn(manager);

        AuthenticationManager result = securityConfig.authenticationManager(authConfig);

        assertEquals(manager, result);
    }
}
