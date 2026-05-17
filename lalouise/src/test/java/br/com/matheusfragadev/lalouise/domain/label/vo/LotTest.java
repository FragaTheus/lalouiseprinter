package br.com.matheusfragadev.lalouise.domain.label.vo;

import br.com.matheusfragadev.lalouise.domain.label.exceptions.InvalidLotException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class LotTest {

    private static final String LOT_PATTERN = "^LT[A-Z0-9]{8}$";

    @Test
    void shouldCreateWhenLotIsValid() {
        Lot lot = new Lot("LTABC12345");

        assertEquals("LTABC12345", lot.code());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "LTABCDEFGH",
            "LT12345678",
            "LT1A2B3C4D",
            "LT9Z8Y7X6W"
    })
    void shouldAcceptValidLots(String validLot) {
        assertDoesNotThrow(() -> new Lot(validLot));
    }

    @Test
    void shouldGenerateValidLot() {
        Lot lot = Lot.generate();

        assertAll(
                () -> assertNotNull(lot.code()),
                () -> assertEquals(10, lot.code().length()),
                () -> assertTrue(lot.code().startsWith("LT")),
                () -> assertTrue(lot.code().matches(LOT_PATTERN))
        );
    }

    @Test
    void shouldThrowWhenLotIsNull() {
        InvalidLotException ex = assertThrows(InvalidLotException.class, () -> new Lot(null));
        assertEquals("Codigo nao pode estar vazio", ex.getMessage());
    }

    @Test
    void shouldThrowWhenLotIsBlank() {
        InvalidLotException ex = assertThrows(InvalidLotException.class, () -> new Lot("   "));
        assertEquals("Codigo nao pode estar vazio", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "LTABC123",
            "LTABC1234",
            "ABCD123456",
            "ltabc1234",
            "LTABCD12!@",
            "LTABCD12A"
    })
    void shouldThrowWhenLotHasInvalidFormat(String invalidLot) {
        InvalidLotException ex = assertThrows(InvalidLotException.class, () -> new Lot(invalidLot));
        assertEquals("Codigo invalido", ex.getMessage());
    }

    @Test
    void shouldThrowWhenLotHasLessThanRequiredLength() {
        InvalidLotException ex = assertThrows(InvalidLotException.class, () -> new Lot("LT1234567"));
        assertEquals("Codigo invalido", ex.getMessage());
    }

    @Test
    void shouldThrowWhenLotHasMoreThanRequiredLength() {
        InvalidLotException ex = assertThrows(InvalidLotException.class, () -> new Lot("LT123456789"));
        assertEquals("Codigo invalido", ex.getMessage());
    }
}



