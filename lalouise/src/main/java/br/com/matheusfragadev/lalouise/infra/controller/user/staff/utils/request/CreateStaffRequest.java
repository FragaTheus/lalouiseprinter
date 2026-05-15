package br.com.matheusfragadev.lalouise.infra.controller.user.staff.utils.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateStaffRequest(
        @NotBlank(message = "Nome é obrigatório")
        String nickname,

        @Email(message = "Email deve ser válido")
        @NotBlank(message = "Email é obrigatório")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String password,

        @NotBlank(message = "Confirmação da senha é obrigatória")
        String confirmPassword,

        UUID sectorId
) {
}
