package br.com.matheusfragadev.lalouise.infra.controller.manager.utils.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateManagerRequest(
        @NotBlank(message = "Nome é obrigatório")
        String nickname,

        @Email(message = "Email deve ser válido")
        @NotBlank(message = "Email é obrigatório")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String password,

        @NotBlank(message = "Confirmação da senha é obrigatória")
        String confirmPassword,

        @NotNull(message = "Restaurante é obrigatório")
        UUID restaurantId
) {
}

