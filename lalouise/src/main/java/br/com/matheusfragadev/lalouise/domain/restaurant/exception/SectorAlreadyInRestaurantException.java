package br.com.matheusfragadev.lalouise.domain.restaurant.exception;

public class SectorAlreadyInRestaurantException extends RuntimeException {
    public SectorAlreadyInRestaurantException(String message) {
        super(message);
    }
}

