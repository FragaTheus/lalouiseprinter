package br.com.matheusfragadev.lalouise.domain.base.credentials.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public record Nickname(
        @Column(name = "nickname")
        String value
) {

    private static final String REGEX = "^[a-zA-ZÀ-ú ]+$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    public Nickname {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Nickname não pode ser nulo ou vazio");
        }
        value = value.trim();
        if (value.length() < 3 || value.length() > 30) {
            throw new IllegalArgumentException("Nickname deve ter entre 3 e 30 caracteres");
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Nickname não é válido");
        }
    }



}
