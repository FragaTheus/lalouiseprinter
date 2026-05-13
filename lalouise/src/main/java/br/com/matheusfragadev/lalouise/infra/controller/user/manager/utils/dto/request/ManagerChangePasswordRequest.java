package br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ManagerChangePasswordRequest(
        @NotBlank(message = "Nova senha é obrigatória")
        String newPassword,

        @NotBlank(message = "Confirmação da nova senha é obrigatória")
        String confirmNewPassword
) {
}

