package br.com.matheusfragadev.lalouise.infra.security.details;

public class DisableUserException extends RuntimeException {
    public DisableUserException(String message) {
        super(message);
    }
}
