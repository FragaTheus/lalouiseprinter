package br.com.matheusfragadev.lalouise.infra.controller.auth;

import java.util.UUID;

public record LoginResponse(
        UUID id,
        String nickname,
        String email,
        String role
) {
}
