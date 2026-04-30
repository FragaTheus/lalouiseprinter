package br.com.matheusfragadev.lalouise.infra.security.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET = "minha-chave-super-secreta-com-32bytes!!";
    private static final javax.crypto.SecretKey KEY = buildKey();

    private static javax.crypto.SecretKey buildKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String id, String role) {
        return Jwts.builder()
                .subject(id)
                .claim("role", role)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                .signWith(KEY, Jwts.SIG.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}