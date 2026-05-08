package br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ProductInfo(
        UUID id,
        String name,
        String description,
        boolean active,
        UUID restaurantId,
        Instant createdAt,
        Instant updatedAt
) {
}

