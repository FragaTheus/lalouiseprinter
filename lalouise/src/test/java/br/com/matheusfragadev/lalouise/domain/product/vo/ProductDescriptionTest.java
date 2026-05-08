package br.com.matheusfragadev.lalouise.domain.product.vo;

import br.com.matheusfragadev.lalouise.domain.product.exception.ProductDescriptionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ProductDescriptionTest {

    // caminho feliz
    @Test
    void shouldCreateWhenDescriptionIsValid() {
        ProductDescription desc = new ProductDescription("Frango grelhado com ervas");
        assertEquals("Frango grelhado com ervas", desc.value());
    }

    @Test
    void shouldTrimLeadingAndTrailingSpaces() {
        ProductDescription desc = new ProductDescription("  Prato do dia  ");
        assertEquals("Prato do dia", desc.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Sal grosso", "Pão artesanal", "Suco natural de laranja", "Café especial"})
    void shouldAcceptValidDescriptions(String valid) {
        assertDoesNotThrow(() -> new ProductDescription(valid));
    }

    @Test
    void shouldAcceptDescriptionWithExactlyMinLength() {
        assertDoesNotThrow(() -> new ProductDescription("Sal"));
    }

    @Test
    void shouldAcceptDescriptionWithExactlyMaxLength() {
        assertDoesNotThrow(() -> new ProductDescription("A".repeat(255)));
    }

    // nulo e vazio
    @Test
    void shouldThrowWhenDescriptionIsNull() {
        assertThrows(ProductDescriptionException.class, () -> new ProductDescription(null));
    }

    @Test
    void shouldThrowWhenDescriptionIsBlank() {
        assertThrows(ProductDescriptionException.class, () -> new ProductDescription("   "));
    }

    // tamanho
    @Test
    void shouldThrowWhenDescriptionIsTooShort() {
        ProductDescriptionException ex = assertThrows(
                ProductDescriptionException.class, () -> new ProductDescription("AB")
        );
        assertEquals("Descrição do produto deve ter entre 3 e 255 caracteres", ex.getMessage());
    }

    @Test
    void shouldThrowWhenDescriptionIsTooLong() {
        assertThrows(ProductDescriptionException.class, () -> new ProductDescription("A".repeat(256)));
    }

    // caracteres invalidos
    @ParameterizedTest
    @ValueSource(strings = {"Frango2", "Pizza@", "Suco#123", "Café!", "Pão/assado"})
    void shouldThrowWhenDescriptionContainsInvalidCharacters(String invalid) {
        assertThrows(ProductDescriptionException.class, () -> new ProductDescription(invalid));
    }

    // mensagens da excecao
    @Test
    void exceptionShouldKeepMessageWhenBlank() {
        ProductDescriptionException ex = assertThrows(
                ProductDescriptionException.class, () -> new ProductDescription("")
        );
        assertEquals("Descrição do produto não pode ser nula ou vazia", ex.getMessage());
    }

    @Test
    void exceptionShouldKeepMessageWhenTooShort() {
        ProductDescriptionException ex = assertThrows(
                ProductDescriptionException.class, () -> new ProductDescription("AB")
        );
        assertEquals("Descrição do produto deve ter entre 3 e 255 caracteres", ex.getMessage());
    }

    @Test
    void exceptionShouldKeepMessageWhenInvalidChars() {
        ProductDescriptionException ex = assertThrows(
                ProductDescriptionException.class, () -> new ProductDescription("Frango2")
        );
        assertEquals("Descrição do produto contém caracteres inválidos", ex.getMessage());
    }
}

