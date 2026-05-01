package br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.response;

import java.util.UUID;

public record AdminResponse(
        UUID id,
        String nickname,
        String email,
        String role,
        boolean active
) implements ProfileResponse {
}
