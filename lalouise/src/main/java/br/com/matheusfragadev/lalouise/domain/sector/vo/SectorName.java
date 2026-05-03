package br.com.matheusfragadev.lalouise.domain.sector.vo;

import br.com.matheusfragadev.lalouise.domain.sector.exception.SectorNameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public record SectorName(
        @Column(name = "name")
        String value
) {
    private static final String REGEX = "^[a-zA-ZÀ-ÿ ]+$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 30;

    public SectorName {
        if (value == null || value.isBlank()) {
            throw new SectorNameException("Nome do setor não pode ser nulo ou vazio");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new SectorNameException(
                    "Nome do setor deve ter entre " + MIN_LENGTH + " e " + MAX_LENGTH + " caracteres"
            );
        }
        if (PATTERN.matcher(value).matches() == false) {
            throw new SectorNameException("Nome do setor contém caracteres inválidos");
        }
    }
}
