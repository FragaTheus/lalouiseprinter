package br.com.matheusfragadev.lalouise.domain.base.credentials.vo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NicknameTest {

    @Test
    void shouldCreateNicknameWhenValueIsValid() {
        Nickname nickname = new Nickname(" Admin User ");

        assertEquals("Admin User", nickname.value());
    }

    @Test
    void shouldThrowWhenNicknameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Nickname("   "));
    }

    @Test
    void shouldThrowWhenNicknameContainsInvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () -> new Nickname("A1"));
    }
}

