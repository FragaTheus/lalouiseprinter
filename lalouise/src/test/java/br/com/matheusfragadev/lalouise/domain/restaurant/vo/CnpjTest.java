package br.com.matheusfragadev.lalouise.domain.restaurant.vo;

import br.com.matheusfragadev.lalouise.domain.restaurant.exception.CnpjException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CnpjTest {

    // caminho feliz
    @Test
    void shouldCreateWhenCnpjIsValid() {
        Cnpj cnpj = new Cnpj("11222333000181");
        assertEquals("11222333000181", cnpj.value());
    }

    @Test
    void shouldAcceptFormattedCnpj() {
        Cnpj cnpj = new Cnpj("11.222.333/0001-81");
        assertEquals("11222333000181", cnpj.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "11222333000181",
            "11.222.333/0001-81",
            "45997418000153",
            "45.997.418/0001-53"
    })
    void shouldAcceptValidCnpjs(String validCnpj) {
        assertDoesNotThrow(() -> new Cnpj(validCnpj));
    }

    // nulo e vazio
    @Test
    void shouldThrowWhenCnpjIsNull() {
        assertThrows(CnpjException.class, () -> new Cnpj(null));
    }

    @Test
    void shouldThrowWhenCnpjIsBlank() {
        assertThrows(CnpjException.class, () -> new Cnpj("   "));
    }

    // tamanho
    @Test
    void shouldThrowWhenCnpjHasTooFewDigits() {
        CnpjException ex = assertThrows(
                CnpjException.class, () -> new Cnpj("1122233300018")
        );
        assertEquals("CNPJ deve conter 14 dígitos", ex.getMessage());
    }

    @Test
    void shouldThrowWhenCnpjHasTooManyDigits() {
        assertThrows(CnpjException.class, () -> new Cnpj("112223330001810"));
    }

    // digitos invalidos
    @Test
    void shouldThrowWhenCnpjContainsNonDigitCharacters() {
        CnpjException ex = assertThrows(
                CnpjException.class, () -> new Cnpj("1122233300018A")
        );
        assertEquals("CNPJ deve conter apenas dígitos", ex.getMessage());
    }

    // todos os digitos iguais
    @ParameterizedTest
    @ValueSource(strings = {
            "00000000000000",
            "11111111111111",
            "99999999999999"
    })
    void shouldThrowWhenAllDigitsAreEqual(String cnpj) {
        CnpjException ex = assertThrows(CnpjException.class, () -> new Cnpj(cnpj));
        assertEquals("CNPJ inválido", ex.getMessage());
    }

    // digitos verificadores invalidos
    @Test
    void shouldThrowWhenCheckDigitsAreInvalid() {
        CnpjException ex = assertThrows(
                CnpjException.class, () -> new Cnpj("11222333000100")
        );
        assertEquals("CNPJ inválido", ex.getMessage());
    }

    // mensagens da excecao
    @Test
    void exceptionShouldKeepMessageWhenBlank() {
        CnpjException ex = assertThrows(
                CnpjException.class, () -> new Cnpj("")
        );
        assertEquals("CNPJ não pode ser nulo ou vazio", ex.getMessage());
    }

    @Test
    void exceptionShouldKeepMessageWhenWrongLength() {
        CnpjException ex = assertThrows(
                CnpjException.class, () -> new Cnpj("123")
        );
        assertEquals("CNPJ deve conter 14 dígitos", ex.getMessage());
    }
}

