package br.com.matheusfragadev.lalouise.domain.user.admin.exceptions;

public class AdminAlreadyExists extends RuntimeException {
    public AdminAlreadyExists(String message) {
        super(message);
    }
}
