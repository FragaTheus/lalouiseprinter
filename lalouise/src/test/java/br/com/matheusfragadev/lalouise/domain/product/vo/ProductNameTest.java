package br.com.matheusfragadev.lalouise.domain.product.vo;

import br.com.matheusfragadev.lalouise.domain.product.exception.ProductNameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ProductNameTest {

    // caminho feliz
    @Test
    void shouldCreateWhenNameIsValid() {
        ProductName name = new ProductName("Frango Grelhado");
        assertEquals("Frango Grelhado", name.value());
    }

    @Test
    void shouldTrimLeadingAndTrailingSpaces() {
        ProductName name = new ProductName("  Pizza  ");
        assertEquals("Pizza", name.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Sal", "Pão", "Frango Grelhado", "Suco de Laranja", "Café"})
    void shouldAcceptValidNames(String validName) {
        assertDoesNotThrow(() -> new ProductName(validName));
    }

    @Test
    void shouldAcceptNameWithExactlyMinLength() {
        assertDoesNotThrow(() -> new ProductName("Sal"));
    }

    @Test
    void shouldAcceptNameWithExactlyMaxLength() {
        assertDoesNotThrow(() -> new ProductName("A".repeat(30)));
    }

    // nulo e vazio
    @Test
    void shouldThrowWhenNameIsNull() {
        assertThrows(ProductNameException.class, () -> new ProductName(null));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThrows(ProductNameException.class, () -> new ProductName("   "));
    }

    // tamanho
    @Test
    void shouldThrowWhenNameIsTooShort() {
        ProductNameException ex = assertThrows(
                ProductNameException.class, () -> new ProductName("AB")
        );
        assertEquals("Nome do produto deve ter entre 3 e 30 caracteres", ex.getMessage());
    }

    @Test
    void shouldThrowWhenNameIsTooLong() {
        assertThrows(ProductNameException.class, () -> new ProductName("A".repeat(31)));
    }

    // caracteres invalidos
    @ParameterizedTest
    @ValueSource(strings = {"Frango2", "Pizza@", "Suco#", "Café!", "Pão/123"})
    void shouldThrowWhenNameContainsInvalidCharacters(String invalidName) {
        assertThrows(ProductNameException.class, () -> new ProductName(invalidName));
    }

    // mensagens da excecao
    @Test
    void exceptionShouldKeepMessageWhenBlank() {
        ProductNameException ex = assertThrows(
                ProductNameException.class, () -> new ProductName("")
        );
        assertEquals("Nome do produto não pode ser nulo ou vazio", ex.getMessage());
    }

    @Test
    void exceptionShouldKeepMessageWhenTooShort() {
        ProductNameException ex = assertThrows(
                ProductNameException.class, () -> new ProductName("AB")
        );
        assertEquals("Nome do produto deve ter entre 3 e 30 caracteres", ex.getMessage());
    }

    @Test
    void exceptionShouldKeepMessageWhenInvalidChars() {
        ProductNameException ex = assertThrows(
                ProductNameException.class, () -> new ProductName("Frango2")
        );
        assertEquals("Nome do produto contém caracteres inválidos", ex.getMessage());
    }
}

