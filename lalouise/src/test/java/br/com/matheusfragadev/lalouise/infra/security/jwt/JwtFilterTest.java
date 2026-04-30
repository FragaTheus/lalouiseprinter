package br.com.matheusfragadev.lalouise.infra.security.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HandlerExceptionResolver exceptionResolver;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueChainWhenAuthorizationHeaderIsMissing() throws Exception {
        JwtFilter jwtFilter = new JwtFilter(jwtService, exceptionResolver);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSetAuthenticationWhenTokenIsValid() throws Exception {
        JwtFilter jwtFilter = new JwtFilter(jwtService, exceptionResolver);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Claims claims = org.mockito.Mockito.mock(Claims.class);

        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
        when(jwtService.extractClaims("valid-token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("123");
        when(claims.get("role", String.class)).thenReturn("admin");

        jwtFilter.doFilter(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("123", authentication.getPrincipal());
        assertEquals("ADMIN", authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void shouldDelegateToExceptionResolverWhenTokenParsingFails() throws Exception {
        JwtFilter jwtFilter = new JwtFilter(jwtService, exceptionResolver);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RuntimeException error = new RuntimeException("invalid token");

        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid-token");
        when(jwtService.extractClaims("invalid-token")).thenThrow(error);

        jwtFilter.doFilter(request, response, filterChain);

        verify(exceptionResolver).resolveException(eq(request), eq(response), eq(null), eq(error));
    }
}

