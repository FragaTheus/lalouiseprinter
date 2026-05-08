package br.com.matheusfragadev.lalouise.domain.product.vo;

import br.com.matheusfragadev.lalouise.domain.product.exception.ProductNameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public record ProductName(
        @Column(name = "name")
        String value
) {
    private static final String REGEX = "^[a-zA-ZÀ-ÿ ]+$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 30;

    public ProductName {
        if (value == null || value.isBlank()) {
            throw new ProductNameException("Nome do produto não pode ser nulo ou vazio");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new ProductNameException(
                    "Nome do produto deve ter entre " + MIN_LENGTH + " e " + MAX_LENGTH + " caracteres"
            );
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new ProductNameException("Nome do produto contém caracteres inválidos");
        }
    }
}

