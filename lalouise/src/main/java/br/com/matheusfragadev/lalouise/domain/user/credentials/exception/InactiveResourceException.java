package br.com.matheusfragadev.lalouise.domain.user.credentials.exception;

public class InactiveResourceException extends RuntimeException {
    public InactiveResourceException(String message) {
        super(message);
    }
}

