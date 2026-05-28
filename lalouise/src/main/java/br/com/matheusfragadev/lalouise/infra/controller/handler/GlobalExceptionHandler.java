package br.com.matheusfragadev.lalouise.infra.controller.handler;

import br.com.matheusfragadev.lalouise.domain.label.exceptions.InvalidLabelStateException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.CnpjException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantActiveException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantNameException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantNotFoundException;
import br.com.matheusfragadev.lalouise.domain.sector.exception.StorageException;
import br.com.matheusfragadev.lalouise.domain.user.admin.exceptions.UserAlreadyExists;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.*;
import br.com.matheusfragadev.lalouise.application.auth.AccountLockedException;
import br.com.matheusfragadev.lalouise.infra.security.details.DisableUserException;
import br.com.matheusfragadev.lalouise.infra.security.ratelimit.RateLimitException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HandlerResponse> handlerMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElseThrow().getDefaultMessage();
        HandlerResponse response = new HandlerResponse(errorMessage);
        log.warn("Validation error: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ActiveException.class)
    public ResponseEntity<HandlerResponse> handleActiveException(ActiveException ex) {
        HandlerResponse response = new HandlerResponse(ex.getMessage());
        log.error("ActiveException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InactiveResourceException.class)
    public ResponseEntity<HandlerResponse> handleInactiveResourceException(InactiveResourceException ex) {
        HandlerResponse response = new HandlerResponse(ex.getMessage());
        log.warn("InactiveResourceException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<HandlerResponse> handleEmailException(EmailException ex) {
        HandlerResponse response = new HandlerResponse(ex.getMessage());
        log.warn("EmailException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<HandlerResponse> handlePasswordException(PasswordException ex) {
        HandlerResponse response = new HandlerResponse(ex.getMessage());
        log.warn("PasswordException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NicknameException.class)
    public ResponseEntity<HandlerResponse> handleNicknameException(NicknameException ex) {
        HandlerResponse response = new HandlerResponse(ex.getMessage());
        log.warn("NicknameException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<HandlerResponse> handleRestaurantNotFoundException(RestaurantNotFoundException ex) {
        HandlerResponse response = new HandlerResponse(ex.getMessage());
        log.warn("RestaurantNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CnpjException.class)
    public ResponseEntity<HandlerResponse> handleCnpjException(CnpjException ex) {
        HandlerResponse response = new HandlerResponse(ex.getMessage());
        log.warn("CnpjException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RestaurantNameException.class)
    public ResponseEntity<HandlerResponse> handleRestaurantNameException(RestaurantNameException ex) {
        HandlerResponse response = new HandlerResponse(ex.getMessage());
        log.warn("RestaurantNameException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RestaurantActiveException.class)
    public ResponseEntity<HandlerResponse> handleRestaurantActiveException(RestaurantActiveException ex) {
        HandlerResponse response = new HandlerResponse(ex.getMessage());
        log.warn("RestaurantActiveException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<HandlerResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        HandlerResponse response = new HandlerResponse(ex.getMessage());
        log.warn("UsernameNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DisableUserException.class)
    public ResponseEntity<HandlerResponse> handleDisableUserException(DisableUserException ex) {
        HandlerResponse response = new HandlerResponse(ex.getMessage());
        log.warn("DisableUserException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<HandlerResponse> handleExpired(ExpiredJwtException e) {
        HandlerResponse response = new HandlerResponse("Sessão expirada, faça login novamente");
        log.warn("ExpiredJwtException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<HandlerResponse> handleInvalid(SignatureException e) {
        HandlerResponse response = new HandlerResponse("Token inválido");
        log.warn("SignatureException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<HandlerResponse> handleMalformed(MalformedJwtException e) {
        HandlerResponse response = new HandlerResponse("Token inválido");
        log.warn("MalformedJwtException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HandlerResponse> handleBadCredentials(BadCredentialsException e) {
        HandlerResponse response = new HandlerResponse(e.getMessage());
        log.warn("BadCredentialsException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<HandlerResponse> handleInternalAuth(InternalAuthenticationServiceException e) {
        log.error("InternalAuth cause: {}", e.getCause());
        Throwable cause = e.getCause();
        if (cause instanceof DisableUserException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new HandlerResponse(cause.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new HandlerResponse("Credenciais inválidas"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HandlerResponse> handleGenericException(Exception ex) {
        HandlerResponse response = new HandlerResponse
                ("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.");
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<HandlerResponse> handleAdminAlreadyExists(UserAlreadyExists ex) {
        HandlerResponse response = new HandlerResponse(ex.getMessage());
        log.warn("AdminAlreadyExists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HandlerResponse> handleAccessDenied(AccessDeniedException ex){
        var response = new HandlerResponse("Acesso negado: você não tem permissão para acessar este recurso.");
        log.warn("AccessDeniedException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<HandlerResponse> handleStorageException(StorageException ex){
        var response = new HandlerResponse(ex.getMessage());
        log.warn("StorageException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(InvalidLabelStateException.class)
    public ResponseEntity<HandlerResponse> handleInvalidLabelState(InvalidLabelStateException ex){
        var response = new HandlerResponse(ex.getMessage());
        log.warn("InvalidLabelStateException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<HandlerResponse> handleAccountLocked(AccountLockedException ex){
        var response = new HandlerResponse(ex.getMessage());
        log.warn("AccountLockException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<HandlerResponse> handleRateLimitExceeded(RateLimitException e) {
        log.warn("⚠️ Rate limit excedido");
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", "60")
                .body(new HandlerResponse(e.getMessage()));
    }

}
