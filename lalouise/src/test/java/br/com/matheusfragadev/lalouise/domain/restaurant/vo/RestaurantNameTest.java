package br.com.matheusfragadev.lalouise.domain.restaurant.vo;

import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantNameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantNameTest {

    // caminho feliz
    @Test
    void shouldCreateWhenNameIsValid() {
        RestaurantName name = new RestaurantName("La Louise");
        assertEquals("La Louise", name.value());
    }

    @Test
    void shouldTrimLeadingAndTrailingSpaces() {
        RestaurantName name = new RestaurantName("  La Louise  ");
        assertEquals("La Louise", name.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"BK", "Sal & Brasa", "Sr. Frango", "Bar 25", "Cafe-Bistro"})
    void shouldAcceptValidNames(String validName) {
        assertDoesNotThrow(() -> new RestaurantName(validName));
    }

    @Test
    void shouldAcceptNameWithExactlyMinLength() {
        assertDoesNotThrow(() -> new RestaurantName("AB"));
    }

    @Test
    void shouldAcceptNameWithExactlyMaxLength() {
        assertDoesNotThrow(() -> new RestaurantName("A".repeat(80)));
    }

    // nulo e vazio
    @Test
    void shouldThrowWhenNameIsNull() {
        assertThrows(RestaurantNameException.class, () -> new RestaurantName(null));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThrows(RestaurantNameException.class, () -> new RestaurantName("   "));
    }

    // tamanho
    @Test
    void shouldThrowWhenNameIsTooShort() {
        RestaurantNameException ex = assertThrows(
                RestaurantNameException.class, () -> new RestaurantName("A")
        );
        assertEquals("Nome do restaurante deve ter entre 2 e 80 caracteres", ex.getMessage());
    }

    @Test
    void shouldThrowWhenNameIsTooLong() {
        assertThrows(RestaurantNameException.class, () -> new RestaurantName("A".repeat(81)));
    }

    // caracteres invalidos
    @ParameterizedTest
    @ValueSource(strings = {"Bar@Joao", "Rest#1", "Food/Place", "Bar[Sul]", "Rest{Norte}"})
    void shouldThrowWhenNameContainsInvalidCharacters(String invalidName) {
        assertThrows(RestaurantNameException.class, () -> new RestaurantName(invalidName));
    }

    // mensagens da excecao
    @Test
    void exceptionShouldKeepMessageWhenBlank() {
        RestaurantNameException ex = assertThrows(
                RestaurantNameException.class, () -> new RestaurantName("")
        );
        assertEquals("Nome do restaurante não pode ser nulo ou vazio", ex.getMessage());
    }

    @Test
    void exceptionShouldKeepMessageWhenTooShort() {
        RestaurantNameException ex = assertThrows(
                RestaurantNameException.class, () -> new RestaurantName("A")
        );
        assertEquals("Nome do restaurante deve ter entre 2 e 80 caracteres", ex.getMessage());
    }

    @Test
    void exceptionShouldKeepMessageWhenInvalidChars() {
        RestaurantNameException ex = assertThrows(
                RestaurantNameException.class, () -> new RestaurantName("Bar@Joao")
        );
        assertEquals("Nome do restaurante contém caracteres inválidos", ex.getMessage());
    }
}
