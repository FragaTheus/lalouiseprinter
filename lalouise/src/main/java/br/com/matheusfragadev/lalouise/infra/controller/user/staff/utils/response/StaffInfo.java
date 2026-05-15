package br.com.matheusfragadev.lalouise.infra.controller.user.staff.utils.response;

import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record StaffInfo(
        UUID id,
        String nickname,
        String email,
        Role role,
        boolean active,
        String sectorName,
        String restaurantName,
        Instant createdAt,
        Instant updatedAt
) {
}
