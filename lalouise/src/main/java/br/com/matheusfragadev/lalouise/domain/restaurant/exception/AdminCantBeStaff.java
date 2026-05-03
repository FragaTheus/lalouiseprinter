package br.com.matheusfragadev.lalouise.domain.restaurant.exception;

public class AdminCantBeStaff extends RuntimeException {
    public AdminCantBeStaff(String message) {
        super(message);
    }
}
