package br.com.matheusfragadev.lalouise.domain.sector.exception;

public class SectorAlreadyExistsException extends RuntimeException {

    public SectorAlreadyExistsException(String message) {
        super(message);
    }
}

