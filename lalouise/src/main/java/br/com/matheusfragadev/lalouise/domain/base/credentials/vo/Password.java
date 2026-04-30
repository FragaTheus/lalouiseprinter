package br.com.matheusfragadev.lalouise.domain.base.credentials.vo;

import br.com.matheusfragadev.lalouise.domain.base.credentials.exception.PasswordException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

@Embeddable
public final class Password {

    @Column(name = "password")
    private String value;

    private Password() {
    }

    private Password(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    private static final String REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d\\s]).+$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    public static Password of(String value, Function<String, String> hasher) {
        if (value == null || value.isBlank()) {
            throw new PasswordException("Senha não pode ser nula ou vazia");
        }
        if (value.length() < 8 || value.length() > 16) {
            throw new PasswordException("Senha deve ter entre 8 e 16 caracteres");
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new PasswordException("Senha não atende aos requisitos de segurança");
        }
        return new Password(hasher.apply(value));
    }
}
