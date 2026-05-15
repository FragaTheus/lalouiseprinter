package br.com.matheusfragadev.lalouise.domain.user.admin.exceptions;

public class UserAlreadyExists extends RuntimeException {
    public UserAlreadyExists(String message) {
        super(message);
    }
}
