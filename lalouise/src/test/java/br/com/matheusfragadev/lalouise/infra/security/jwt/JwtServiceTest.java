package br.com.matheusfragadev.lalouise.infra.security.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void shouldGenerateTokenAndExtractClaims() {
        String token = jwtService.generateToken("123", "ADMIN");

        Claims claims = jwtService.extractClaims(token);

        assertEquals("123", claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
        assertTrue(claims.getExpiration().toInstant().isAfter(Instant.now()));
    }
}

