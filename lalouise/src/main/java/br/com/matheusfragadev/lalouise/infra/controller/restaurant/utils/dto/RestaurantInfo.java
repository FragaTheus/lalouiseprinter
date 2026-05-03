package br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RestaurantInfo(
        UUID id,
        String name,
        String cnpj,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
