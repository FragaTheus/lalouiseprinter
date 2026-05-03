package br.com.matheusfragadev.lalouise.domain.sector.vo;

import br.com.matheusfragadev.lalouise.domain.sector.exception.SectorNameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SectorNameTest {

    // caminho feliz
    @Test
    void shouldCreateWhenNameIsValid() {
        SectorName name = new SectorName("Cozinha");
        assertEquals("Cozinha", name.value());
    }

    @Test
    void shouldTrimLeadingAndTrailingSpaces() {
        SectorName name = new SectorName("  Bar  ");
        assertEquals("Bar", name.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"AB", "Salão", "Cozinha Fria", "Copa", "Estoque"})
    void shouldAcceptValidNames(String validName) {
        assertDoesNotThrow(() -> new SectorName(validName));
    }

    @Test
    void shouldAcceptNameWithExactlyMinLength() {
        assertDoesNotThrow(() -> new SectorName("AB"));
    }

    @Test
    void shouldAcceptNameWithExactlyMaxLength() {
        assertDoesNotThrow(() -> new SectorName("A".repeat(30)));
    }

    // nulo e vazio
    @Test
    void shouldThrowWhenNameIsNull() {
        assertThrows(SectorNameException.class, () -> new SectorName(null));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThrows(SectorNameException.class, () -> new SectorName("   "));
    }

    // tamanho
    @Test
    void shouldThrowWhenNameIsTooShort() {
        SectorNameException ex = assertThrows(
                SectorNameException.class, () -> new SectorName("A")
        );
        assertEquals("Nome do setor deve ter entre 2 e 30 caracteres", ex.getMessage());
    }

    @Test
    void shouldThrowWhenNameIsTooLong() {
        assertThrows(SectorNameException.class, () -> new SectorName("A".repeat(31)));
    }

    // caracteres invalidos
    @ParameterizedTest
    @ValueSource(strings = {"Cozinha1", "Bar@", "Salão#", "Copa!", "Estoque/2"})
    void shouldThrowWhenNameContainsInvalidCharacters(String invalidName) {
        assertThrows(SectorNameException.class, () -> new SectorName(invalidName));
    }

    // mensagens da excecao
    @Test
    void exceptionShouldKeepMessageWhenBlank() {
        SectorNameException ex = assertThrows(
                SectorNameException.class, () -> new SectorName("")
        );
        assertEquals("Nome do setor não pode ser nulo ou vazio", ex.getMessage());
    }

    @Test
    void exceptionShouldKeepMessageWhenTooShort() {
        SectorNameException ex = assertThrows(
                SectorNameException.class, () -> new SectorName("A")
        );
        assertEquals("Nome do setor deve ter entre 2 e 30 caracteres", ex.getMessage());
    }

    @Test
    void exceptionShouldKeepMessageWhenInvalidChars() {
        SectorNameException ex = assertThrows(
                SectorNameException.class, () -> new SectorName("Cozinha1")
        );
        assertEquals("Nome do setor contém caracteres inválidos", ex.getMessage());
    }
}
