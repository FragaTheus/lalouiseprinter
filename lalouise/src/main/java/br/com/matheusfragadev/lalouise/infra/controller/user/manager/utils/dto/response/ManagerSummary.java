package br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ManagerSummary(
        UUID id,
        String nickname,
        String email,
        boolean active,
        UUID restaurantId
) {
}

