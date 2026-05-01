package br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password
) {
}
