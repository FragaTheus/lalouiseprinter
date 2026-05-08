package br.com.matheusfragadev.lalouise.domain.product.exception;

public class ProductActiveException extends RuntimeException {
    public ProductActiveException(String message) {
        super(message);
    }
}

