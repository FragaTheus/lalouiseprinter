package br.com.matheusfragadev.lalouise.infra.controller.handler;

import br.com.matheusfragadev.lalouise.domain.restaurant.exception.CnpjException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantActiveException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantNameException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantNotFoundException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.ActiveException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.EmailException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.infra.security.details.DisableUserException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ── credentials ───────────────────────────────────────────────────────────
    @Test
    void shouldHandleActiveException() {
        var response = handler.handleActiveException(new ActiveException("ativo"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("ativo", response.getBody().error());
    }

    @Test
    void shouldHandleEmailException() {
        var response = handler.handleEmailException(new EmailException("email"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("email", response.getBody().error());
    }

    @Test
    void shouldHandlePasswordException() {
        var response = handler.handlePasswordException(new PasswordException("senha"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("senha", response.getBody().error());
    }

    @Test
    void shouldHandleNicknameException() {
        var response = handler.handleNicknameException(new NicknameException("nick"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("nick", response.getBody().error());
    }

    @Test
    void shouldHandleUsernameNotFoundException() {
        var response = handler.handleUsernameNotFoundException(new UsernameNotFoundException("nao encontrado"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("nao encontrado", response.getBody().error());
    }

    @Test
    void shouldHandleDisableUserException() {
        var response = handler.handleDisableUserException(new DisableUserException("inativo"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("inativo", response.getBody().error());
    }

    // ── restaurant ────────────────────────────────────────────────────────────
    @Test
    void shouldHandleRestaurantNotFoundException() {
        var response = handler.handleRestaurantNotFoundException(new RestaurantNotFoundException("Restaurante não encontrado"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Restaurante não encontrado", response.getBody().error());
    }

    @Test
    void shouldHandleCnpjException() {
        var response = handler.handleCnpjException(new CnpjException("CNPJ inválido"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("CNPJ inválido", response.getBody().error());
    }

    @Test
    void shouldHandleRestaurantNameException() {
        var response = handler.handleRestaurantNameException(new RestaurantNameException("Nome inválido"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Nome inválido", response.getBody().error());
    }

    @Test
    void shouldHandleRestaurantActiveException() {
        var response = handler.handleRestaurantActiveException(new RestaurantActiveException("Restaurante já está inativo"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Restaurante já está inativo", response.getBody().error());
    }

    // ── jwt / auth ────────────────────────────────────────────────────────────
    @Test
    void shouldHandleExpiredJwtException() {
        var response = handler.handleExpired(new ExpiredJwtException(null, null, "expired"));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Sessão expirada, faça login novamente", response.getBody().error());
    }

    @Test
    void shouldHandleSignatureException() {
        var response = handler.handleInvalid(new SignatureException("invalid"));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Token inválido", response.getBody().error());
    }

    @Test
    void shouldHandleMalformedJwtException() {
        var response = handler.handleMalformed(new MalformedJwtException("malformed"));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Token inválido", response.getBody().error());
    }

    @Test
    void shouldHandleBadCredentialsException() {
        var response = handler.handleBadCredentials(new BadCredentialsException("credenciais inválidas"));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("credenciais inválidas", response.getBody().error());
    }

    @Test
    void shouldHandleUnexpectedException() {
        var response = handler.handleGenericException(new RuntimeException("boom"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.", response.getBody().error());
    }
}
