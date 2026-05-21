package br.com.matheusfragadev.lalouise.domain.label.exceptions;

public class InvalidLabelStateException extends RuntimeException {
    public InvalidLabelStateException(String message) {
        super(message);
    }
}
