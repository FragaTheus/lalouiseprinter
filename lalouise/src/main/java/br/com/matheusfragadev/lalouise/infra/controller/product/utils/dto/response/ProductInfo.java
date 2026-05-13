package br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response;

import br.com.matheusfragadev.lalouise.domain.product.enums.Category;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ProductInfo(
        UUID id,
        String name,
        Category category,
        boolean active,
        UUID restaurantId,
        Instant createdAt,
        Instant updatedAt
) {
}

