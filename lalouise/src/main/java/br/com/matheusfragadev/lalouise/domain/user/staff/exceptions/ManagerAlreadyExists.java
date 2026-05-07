package br.com.matheusfragadev.lalouise.domain.user.staff.exceptions;

public class ManagerAlreadyExists extends RuntimeException {
    public ManagerAlreadyExists(String message) {
        super(message);
    }
}

