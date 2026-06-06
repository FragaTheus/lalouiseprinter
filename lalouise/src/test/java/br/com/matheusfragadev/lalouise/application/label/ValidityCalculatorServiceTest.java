package br.com.matheusfragadev.lalouise.application.label;

import br.com.matheusfragadev.lalouise.application.label.exception.ValidityCalculationException;
import br.com.matheusfragadev.lalouise.domain.product.enums.Category;
import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class ValidityCalculatorServiceTest {

    private ValidityCalculatorService service;

    @BeforeEach
    void setUp() {
        service = new ValidityCalculatorService();
    }

    // --- Fallback: categorias sem regra específica devem usar base por storage ---

    @Test
    void shouldReturn1DayForAmbientWhenCategoryHasNoSpecificRule() {
        Instant before = Instant.now();
        Instant result = service.calculate(Category.VEGETABLE, Storage.AMBIENT);
        Instant after = Instant.now();

        assertBetween(result, before.plus(1, ChronoUnit.DAYS), after.plus(1, ChronoUnit.DAYS));
    }

    @Test
    void shouldReturn3DaysForRefrigeratedWhenCategoryHasNoSpecificRule() {
        Instant before = Instant.now();
        Instant result = service.calculate(Category.VEGETABLE, Storage.REFRIGERATED);
        Instant after = Instant.now();

        assertBetween(result, before.plus(3, ChronoUnit.DAYS), after.plus(3, ChronoUnit.DAYS));
    }

    @Test
    void shouldReturn10DaysForFrozenWhenCategoryHasNoSpecificRule() {
        Instant before = Instant.now();
        Instant result = service.calculate(Category.GRAINS, Storage.FROZEN);
        Instant after = Instant.now();

        assertBetween(result, before.plus(10, ChronoUnit.DAYS), after.plus(10, ChronoUnit.DAYS));
    }

    @Test
    void shouldReturn30DaysForDeepFrozenWhenCategoryHasNoSpecificRule() {
        Instant before = Instant.now();
        Instant result = service.calculate(Category.VEGETABLE, Storage.DEEP_FROZEN);
        Instant after = Instant.now();

        assertBetween(result, before.plus(30, ChronoUnit.DAYS), after.plus(30, ChronoUnit.DAYS));
    }

    // --- Regra específica de categoria: SEAFOOD tem REFRIGERATED=2 ---

    @Test
    void shouldReturn2DaysForSeafoodRefrigerated() {
        Instant before = Instant.now();
        Instant result = service.calculate(Category.SEAFOOD, Storage.REFRIGERATED);
        Instant after = Instant.now();

        assertBetween(result, before.plus(2, ChronoUnit.DAYS), after.plus(2, ChronoUnit.DAYS));
    }

    @Test
    void shouldReturn1DayForSeafoodAmbient() {
        Instant before = Instant.now();
        Instant result = service.calculate(Category.SEAFOOD, Storage.AMBIENT);
        Instant after = Instant.now();

        assertBetween(result, before.plus(1, ChronoUnit.DAYS), after.plus(1, ChronoUnit.DAYS));
    }

    @Test
    void shouldReturn7DaysForSeafoodFrozen() {
        Instant before = Instant.now();
        Instant result = service.calculate(Category.SEAFOOD, Storage.FROZEN);
        Instant after = Instant.now();

        assertBetween(result, before.plus(7, ChronoUnit.DAYS), after.plus(7, ChronoUnit.DAYS));
    }

    @Test
    void shouldReturn20DaysForSeafoodDeepFrozen() {
        Instant before = Instant.now();
        Instant result = service.calculate(Category.SEAFOOD, Storage.DEEP_FROZEN);
        Instant after = Instant.now();

        assertBetween(result, before.plus(20, ChronoUnit.DAYS), after.plus(20, ChronoUnit.DAYS));
    }

    // --- Fallback cobre todas as categorias restantes ---

    @ParameterizedTest
    @EnumSource(value = Category.class, names = {"VEGETABLE", "GRAINS", "SEASONINGS"})
    void shouldUseFallbackForCategoriesWithoutSpecificRules(Category category) {
        assertDoesNotThrow(() -> service.calculate(category, Storage.AMBIENT));
        assertDoesNotThrow(() -> service.calculate(category, Storage.REFRIGERATED));
        assertDoesNotThrow(() -> service.calculate(category, Storage.FROZEN));
        assertDoesNotThrow(() -> service.calculate(category, Storage.DEEP_FROZEN));
    }

    // --- Exceções por nulos ---

    @Test
    void shouldThrowWhenCategoryIsNull() {
        ValidityCalculationException ex = assertThrows(
                ValidityCalculationException.class,
                () -> service.calculate(null, Storage.REFRIGERATED)
        );
        assertEquals("Categoria do produto não pode ser nula.", ex.getMessage());
    }

    @Test
    void shouldThrowWhenStorageIsNull() {
        ValidityCalculationException ex = assertThrows(
                ValidityCalculationException.class,
                () -> service.calculate(Category.PROTEIN, null)
        );
        assertEquals("Tipo de armazenamento não pode ser nulo.", ex.getMessage());
    }

    // --- Helper ---

    private void assertBetween(Instant result, Instant from, Instant to) {
        assertFalse(result.isBefore(from), "Expected result >= " + from + " but was " + result);
        assertFalse(result.isAfter(to),    "Expected result <= " + to   + " but was " + result);
    }
}
