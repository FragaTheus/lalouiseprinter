package br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProductSummary(
        UUID id,
        String name,
        String description,
        boolean active,
        UUID restaurantId
) {
}

