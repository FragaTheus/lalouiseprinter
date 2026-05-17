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

    /**
     * Fallback handler: validade definida somente pelo tipo de armazenamento.
     * Padrão: AMBIENT=1d, REFRIGERATED=3d, FROZEN=30d, DEEP_FROZEN=90d.
     */
    private static final Map<Storage, Integer> FALLBACK = new EnumMap<>(Storage.class);

    /**
     * Regras específicas por categoria. Se a categoria não estiver mapeada,
     * ou o storage não existir dentro dela, cai no FALLBACK.
     */
    private static final Map<Category, Map<Storage, Integer>> RULES = new EnumMap<>(Category.class);

    static {
        FALLBACK.put(Storage.AMBIENT,     1);
        FALLBACK.put(Storage.REFRIGERATED, 3);
        FALLBACK.put(Storage.FROZEN,      30);
        FALLBACK.put(Storage.DEEP_FROZEN, 90);

        // SEAFOOD: mais restritivo em refrigerado (2 dias) — demais caem no fallback
        Map<Storage, Integer> seafood = new EnumMap<>(Storage.class);
        seafood.put(Storage.AMBIENT,      1);
        seafood.put(Storage.REFRIGERATED, 2);
        seafood.put(Storage.FROZEN,       30);
        seafood.put(Storage.DEEP_FROZEN,  90);
        RULES.put(Category.SEAFOOD, seafood);

        // Demais categorias: sem regra específica → caem integralmente no fallback
    }

    /**
     * Calcula a data de validade com base na categoria do produto e no tipo de armazenamento.
     * <p>
     * Fluxo:
     * 1. Busca regras específicas da categoria {@code RULES.get(category)}.
     * 2. Dentro da categoria, busca o storage informado.
     * 3. Se não encontrar (categoria ou storage ausente), cai no {@code FALLBACK} por storage.
     * 4. Se o storage também não existir no fallback, lança {@link ValidityCalculationException}.
     *
     * @param category categoria do produto
     * @param storage  tipo de armazenamento do setor
     * @return {@link Instant} representando a data de validade a partir de agora
     */
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
