package br.com.matheusfragadev.lalouise.application.profile;

import br.com.matheusfragadev.lalouise.application.user.profile.utils.ProfileChangePassword;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProfileChangePasswordTest {

    @Test
    void shouldExposeAllFieldsViaBuilder() {
        UUID id = UUID.randomUUID();
        ProfileChangePassword command = ProfileChangePassword.builder()
                .userId(id)
                .currentPassword("Current@1")
                .newPassword("NewPass@1")
                .confirmNewPassword("NewPass@1")
                .build();

        assertEquals(id, command.userId());
        assertEquals("Current@1", command.currentPassword());
        assertEquals("NewPass@1", command.newPassword());
        assertEquals("NewPass@1", command.confirmNewPassword());
    }

    @Test
    void equalCommandsShouldBeEqual() {
        UUID id = UUID.randomUUID();
        ProfileChangePassword a = ProfileChangePassword.builder()
                .userId(id).currentPassword("A@1").newPassword("B@2").confirmNewPassword("B@2").build();
        ProfileChangePassword b = ProfileChangePassword.builder()
                .userId(id).currentPassword("A@1").newPassword("B@2").confirmNewPassword("B@2").build();
        assertEquals(a, b);
    }
}

