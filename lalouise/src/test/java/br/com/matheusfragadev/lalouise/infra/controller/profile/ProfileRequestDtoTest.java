package br.com.matheusfragadev.lalouise.infra.controller.profile;

import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.request.ChangeNameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.request.ChangePasswordRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileRequestDtoTest {

    @Test
    void changeNameRequestShouldExposeField() {
        ChangeNameRequest request = new ChangeNameRequest("New Name");
        assertEquals("New Name", request.newName());
    }

    @Test
    void changePasswordRequestShouldExposeAllFields() {
        ChangePasswordRequest request = new ChangePasswordRequest("Current@1", "NewPass@1", "NewPass@1");
        assertEquals("Current@1", request.currentPassword());
        assertEquals("NewPass@1", request.newPassword());
        assertEquals("NewPass@1", request.confirmNewPassword());
    }
}

