package br.com.matheusfragadev.lalouise.infra.controller.user.shared;

import jakarta.validation.constraints.NotBlank;

public record UserChangePasswordRequest(
        @NotBlank(message = "Nova senha é obrigatória")
        String newPassword,
        @NotBlank(message = "Confirmação da nova senha é obrigatória")
        String confirmNewPassword
) {
}
