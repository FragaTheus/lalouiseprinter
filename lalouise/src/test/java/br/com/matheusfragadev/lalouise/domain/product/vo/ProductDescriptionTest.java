package br.com.matheusfragadev.lalouise.domain.product.vo;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductNameException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ProductDescriptionTest {
    @Test
    void shouldCreateWhenNameIsValid() {
        ProductName name = new ProductName("Frango grelhado com ervas");
        assertEquals("Frango grelhado com ervas", name.value());
    }
    @Test
    void shouldTrimLeadingAndTrailingSpaces() {
        ProductName name = new ProductName("  Prato do dia  ");
        assertEquals("Prato do dia", name.value());
    }
    @Test
    void shouldAcceptNameWithExactlyMaxLength() {
        assertDoesNotThrow(() -> new ProductName("A".repeat(30)));
    }
    @Test
    void shouldThrowWhenNameIsNullOrBlank() {
        assertAll(
                () -> assertThrows(ProductNameException.class, () -> new ProductName(null)),
                () -> assertThrows(ProductNameException.class, () -> new ProductName("   "))
        );
    }
    @Test
    void shouldThrowWhenNameIsTooShort() {
        ProductNameException ex = assertThrows(ProductNameException.class, () -> new ProductName("AB"));
        assertEquals("Nome do produto deve ter entre 3 e 30 caracteres", ex.getMessage());
    }
    @Test
    void shouldThrowWhenNameContainsInvalidCharacters() {
        ProductNameException ex = assertThrows(ProductNameException.class, () -> new ProductName("Frango2"));
        assertEquals("Nome do produto contém caracteres inválidos", ex.getMessage());
    }
}
