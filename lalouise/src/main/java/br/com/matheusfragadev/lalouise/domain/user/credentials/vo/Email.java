package br.com.matheusfragadev.lalouise.domain.user.credentials.vo;

import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.EmailException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public record Email(
        @Column(name = "email")
        String value
) {
    private static final String REGEX = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    public Email {
        if (value == null || value.isBlank()) {
            throw new EmailException("Email não pode ser nulo ou vazio");
        }
        value = value.trim();
        if (value.length() < 5 || value.length() > 254) {
            throw new EmailException("Email deve ter entre 5 e 254 caracteres");
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new EmailException("Email não é válido");
        }
    }

}
