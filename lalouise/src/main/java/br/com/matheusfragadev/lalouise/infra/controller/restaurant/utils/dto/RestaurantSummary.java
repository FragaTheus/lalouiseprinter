package br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RestaurantSummary(
        UUID restaurantId,
        String restaurantName,
        boolean active
) {
}
