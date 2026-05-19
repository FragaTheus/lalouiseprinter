package br.com.matheusfragadev.lalouise.infra.security.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setup() throws Exception {
        jwtService = new JwtService();

        Field secretField = JwtService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        // 32+ bytes for HS256 key length requirements.
        secretField.set(jwtService, "0123456789abcdef0123456789abcdef");

        Method initMethod = JwtService.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(jwtService);
    }

    @Test
    void shouldGenerateTokenAndExtractClaims() {
        String token = jwtService.generateToken("123", "ADMIN");

        Claims claims = jwtService.extractClaims(token);

        assertEquals("123", claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
        assertTrue(claims.getExpiration().toInstant().isAfter(Instant.now()));
    }
}
