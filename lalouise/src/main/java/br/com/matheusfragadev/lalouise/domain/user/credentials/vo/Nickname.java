package br.com.matheusfragadev.lalouise.domain.user.credentials.vo;

import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
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
            throw new NicknameException("Nome não pode ser nulo ou vazio");
        }
        value = value.trim();
        if (value.length() < 3 || value.length() > 30) {
            throw new NicknameException("Nome deve ter entre 3 e 30 caracteres");
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new NicknameException("Nome não é válido");
        }
    }



}
