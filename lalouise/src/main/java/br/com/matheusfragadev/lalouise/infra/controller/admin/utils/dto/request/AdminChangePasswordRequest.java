package br.com.matheusfragadev.lalouise.infra.controller.admin.utils.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminChangePasswordRequest(
        @NotBlank(message = "Nova senha é obrigatória")
        String newPassword,
        @NotBlank(message = "Confirmação da nova senha é obrigatória")
        String confirmNewPassword
) {
}
