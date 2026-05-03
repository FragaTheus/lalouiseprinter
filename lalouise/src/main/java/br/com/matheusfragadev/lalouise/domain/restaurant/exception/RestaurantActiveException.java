package br.com.matheusfragadev.lalouise.domain.restaurant.exception;

public class RestaurantActiveException extends RuntimeException {
    public RestaurantActiveException(String message) {
        super(message);
    }
}

