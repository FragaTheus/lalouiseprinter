package br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Formato de email inválido")
        String email,
        @NotBlank(message = "Senha é obrigatória")
        String password
) {
}
