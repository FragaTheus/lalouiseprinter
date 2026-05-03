package br.com.matheusfragadev.lalouise.domain.credentials.vo;

import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import org.junit.jupiter.api.Test;

import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;

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
        assertThrows(NicknameException.class, () -> new Nickname("   "));
    }

    @Test
    void shouldThrowWhenNicknameContainsInvalidCharacters() {
        assertThrows(NicknameException.class, () -> new Nickname("A1"));
    }
}

