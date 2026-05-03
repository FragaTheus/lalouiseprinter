package br.com.matheusfragadev.lalouise.domain.restaurant.vo;

import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantNameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public record RestaurantName(
        @Column(name = "name")
        String value
) {
    private static final String REGEX = "^[a-zA-ZÀ-ÿ0-9 '.\\-&]+$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 80;

    public RestaurantName {
        if (value == null || value.isBlank()) {
            throw new RestaurantNameException("Nome do restaurante não pode ser nulo ou vazio");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new RestaurantNameException(
                    "Nome do restaurante deve ter entre " + MIN_LENGTH + " e " + MAX_LENGTH + " caracteres"
            );
        }
        if (PATTERN.matcher(value).matches() == false) {
            throw new RestaurantNameException("Nome do restaurante contém caracteres inválidos");
        }
    }
}
