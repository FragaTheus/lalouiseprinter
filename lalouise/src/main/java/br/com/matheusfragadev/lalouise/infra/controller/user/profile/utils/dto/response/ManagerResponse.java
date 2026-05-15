package br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response;

import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;

import java.time.Instant;
import java.util.UUID;

public record ManagerResponse(
        UUID id,
        String nickname,
        String email,
        String restaurantName,
        Role role,
        Instant createdAt
) implements ProfileResponse {
}

