package br.com.matheusfragadev.lalouise.domain.sector.vo;

import br.com.matheusfragadev.lalouise.domain.sector.exception.SectorDescriptionException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public record SectorDescription(
        @Column(name = "description")
        String value
) {
    private static final String REGEX = "^[a-zA-ZÀ-ÿ0-9 .,\\-]+$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 255;

    public SectorDescription {
        if (value == null || value.isBlank()) {
            throw new SectorDescriptionException("Descrição do setor não pode ser nula ou vazia");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new SectorDescriptionException(
                    "Descrição do setor deve ter entre " + MIN_LENGTH + " e " + MAX_LENGTH + " caracteres"
            );
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new SectorDescriptionException("Descrição do setor contém caracteres inválidos");
        }
    }
}

