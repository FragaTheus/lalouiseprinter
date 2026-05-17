package br.com.matheusfragadev.lalouise.domain.label.exceptions;

public class LabelNotFoundException extends RuntimeException {

    public LabelNotFoundException(String message) {
        super(message);
    }
}
