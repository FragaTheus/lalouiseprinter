package br.com.matheusfragadev.lalouise.infra.security.ratelimit;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}
