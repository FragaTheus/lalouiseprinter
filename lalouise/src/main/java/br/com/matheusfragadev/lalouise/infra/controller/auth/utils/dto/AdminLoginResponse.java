package br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto;

public record AdminLoginResponse(
        String id,
        String nickname,
        String email,
        String role
) implements LoginResponse {
}

