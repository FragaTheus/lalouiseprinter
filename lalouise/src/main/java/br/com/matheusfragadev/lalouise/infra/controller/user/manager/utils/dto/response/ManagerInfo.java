package br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.response;

import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ManagerInfo(
        UUID id,
        String nickname,
        String email,
        Role role,
        boolean active,
        String restaurantName,
        Instant createdAt,
        Instant updatedAt
) {
}

