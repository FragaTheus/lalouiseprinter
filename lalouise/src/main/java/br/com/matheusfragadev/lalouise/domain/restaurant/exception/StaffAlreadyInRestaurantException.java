package br.com.matheusfragadev.lalouise.domain.restaurant.exception;

public class StaffAlreadyInRestaurantException extends RuntimeException {
    public StaffAlreadyInRestaurantException(String message) {
        super(message);
    }
}

