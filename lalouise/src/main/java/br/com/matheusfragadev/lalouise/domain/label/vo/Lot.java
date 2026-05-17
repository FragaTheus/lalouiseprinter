package br.com.matheusfragadev.lalouise.domain.label.vo;

import br.com.matheusfragadev.lalouise.domain.label.exceptions.InvalidLotException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;
import java.util.regex.Pattern;

@Embeddable
public record Lot(
        @Column(name = "lot")
        String code
) {

    private static final String PREFIX = "LT";
    private static final int RANDOM_PART_SIZE = 8;
    private static final Pattern LOT_PATTERN =
            Pattern.compile("^%s[A-Z0-9]{%d}$"
                    .formatted(PREFIX, RANDOM_PART_SIZE));

    public Lot{
        if (code == null || code.isBlank()){
            throw new InvalidLotException("Codigo nao pode estar vazio");
        }

        if (!LOT_PATTERN.matcher(code).matches()){
            throw new InvalidLotException("Codigo invalido");
        }
    }

    public static Lot generate(){
        String generatedCode = PREFIX + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0,RANDOM_PART_SIZE)
                .toUpperCase();
        return new Lot(generatedCode);
    }

}
