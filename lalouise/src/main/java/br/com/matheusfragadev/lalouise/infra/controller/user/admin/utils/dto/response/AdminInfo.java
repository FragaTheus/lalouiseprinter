package br.com.matheusfragadev.lalouise.infra.controller.user.admin.utils.dto.response;

import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record AdminInfo(
        UUID id,
        String nickname,
        String email,
        Instant createdAt,
        boolean active,
        Role role,
        Instant updatedAt
) {
}
