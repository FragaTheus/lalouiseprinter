package br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.response;

import java.time.Instant;
import java.util.UUID;

public record AdminResponse(
        UUID id,
        String nickname,
        String email,
        String role,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) implements ProfileResponse {
}
