package br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeRestaurantNameRequest(
        @NotBlank(message = "Nome do restaurante é obrigatório")
        String restaurantName
) {
}
