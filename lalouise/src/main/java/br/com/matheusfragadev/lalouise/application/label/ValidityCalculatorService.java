package br.com.matheusfragadev.lalouise.application.label;

import br.com.matheusfragadev.lalouise.application.label.exception.ValidityCalculationException;
import br.com.matheusfragadev.lalouise.domain.product.enums.Category;
import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.Map;

@Service
public class ValidityCalculatorService {

    private static final Map<Storage, Integer> FALLBACK = new EnumMap<>(Storage.class);

    private static final Map<Category, Map<Storage, Integer>> RULES = new EnumMap<>(Category.class);

    static {
        FALLBACK.put(Storage.AMBIENT,      1);
        FALLBACK.put(Storage.REFRIGERATED, 3);
        FALLBACK.put(Storage.FROZEN,       10);
        FALLBACK.put(Storage.DEEP_FROZEN,  30);

        Map<Storage, Integer> seafood = new EnumMap<>(Storage.class);
        seafood.put(Storage.AMBIENT,       1);
        seafood.put(Storage.REFRIGERATED,  2);
        seafood.put(Storage.FROZEN,        7);
        seafood.put(Storage.DEEP_FROZEN,   20);
        RULES.put(Category.SEAFOOD, seafood);

        Map<Storage, Integer> protein = new EnumMap<>(Storage.class);
        protein.put(Storage.AMBIENT,       1);
        protein.put(Storage.REFRIGERATED,  2);
        protein.put(Storage.FROZEN,        8);
        protein.put(Storage.DEEP_FROZEN,   25);
        RULES.put(Category.PROTEIN, protein);

        Map<Storage, Integer> sauces = new EnumMap<>(Storage.class);
        sauces.put(Storage.AMBIENT,        1);
        sauces.put(Storage.REFRIGERATED,   3);
        sauces.put(Storage.FROZEN,         15);
        sauces.put(Storage.DEEP_FROZEN,    30);
        RULES.put(Category.SAUCES, sauces);

        Map<Storage, Integer> pasta = new EnumMap<>(Storage.class);
        pasta.put(Storage.AMBIENT,         1);
        pasta.put(Storage.REFRIGERATED,    2);
        pasta.put(Storage.FROZEN,          10);
        pasta.put(Storage.DEEP_FROZEN,     30);
        RULES.put(Category.PASTA, pasta);
    }

    public Instant calculate(Category category, Storage storage) {
        if (category == null) {
            throw new ValidityCalculationException("Categoria do produto não pode ser nula.");
        }
        if (storage == null) {
            throw new ValidityCalculationException("Tipo de armazenamento não pode ser nulo.");
        }

        Map<Storage, Integer> categoryRules = RULES.getOrDefault(category, FALLBACK);
        Integer days = categoryRules.getOrDefault(storage, FALLBACK.get(storage));

        if (days == null) {
            throw new ValidityCalculationException(
                    "Não foi possível calcular a validade para categoria [%s] e armazenamento [%s]."
                            .formatted(category, storage)
            );
        }

        return Instant.now().plus(days, ChronoUnit.DAYS);
    }
}
