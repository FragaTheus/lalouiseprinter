package br.com.matheusfragadev.lalouise.domain.restaurant.vo;

import br.com.matheusfragadev.lalouise.domain.restaurant.exception.CnpjException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Cnpj(
        @Column(name = "cnpj", unique = true)
        String value
) {
    private static final int CNPJ_LENGTH = 14;

    public Cnpj {
        if (value == null || value.isBlank()) {
            throw new CnpjException("CNPJ não pode ser nulo ou vazio");
        }
        value = value.replaceAll("[.\\-/]", "").trim();
        if (value.length() != CNPJ_LENGTH) {
            throw new CnpjException("CNPJ deve conter 14 dígitos");
        }
        if (!value.matches("\\d{14}")) {
            throw new CnpjException("CNPJ deve conter apenas dígitos");
        }
        if (value.chars().distinct().count() == 1) {
            throw new CnpjException("CNPJ inválido");
        }
        if (!isValidCheckDigits(value)) {
            throw new CnpjException("CNPJ inválido");
        }
    }

    private static boolean isValidCheckDigits(String cnpj) {
        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += Character.getNumericValue(cnpj.charAt(i)) * weights1[i];
        }
        int remainder = sum % 11;
        int firstDigit = remainder < 2 ? 0 : 11 - remainder;

        if (Character.getNumericValue(cnpj.charAt(12)) != firstDigit) {
            return false;
        }

        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += Character.getNumericValue(cnpj.charAt(i)) * weights2[i];
        }
        remainder = sum % 11;
        int secondDigit = remainder < 2 ? 0 : 11 - remainder;

        return Character.getNumericValue(cnpj.charAt(13)) == secondDigit;
    }
}

