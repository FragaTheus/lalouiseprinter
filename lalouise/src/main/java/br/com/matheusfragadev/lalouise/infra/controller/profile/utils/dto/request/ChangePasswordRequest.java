package br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "Senha atual deve ser enviada")
        String currentPassword,
        @NotBlank(message = "Nova senha deve ser enviada")
        String newPassword,
        @NotBlank(message = "Confirmação da nova senha deve ser enviada")
        String confirmNewPassword
) {
}
