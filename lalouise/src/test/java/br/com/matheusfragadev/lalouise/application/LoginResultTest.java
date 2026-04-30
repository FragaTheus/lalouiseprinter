package br.com.matheusfragadev.lalouise.application;

import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class LoginResultTest {

    @Test
    void shouldExposeTokenAndUserDetails() {
        UserDetailsImpl userDetails = org.mockito.Mockito.mock(UserDetailsImpl.class);
        LoginResult result = new LoginResult("token", userDetails);

        assertEquals("token", result.token());
        assertSame(userDetails, result.userDetails());
    }
}

