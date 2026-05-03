package br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateRestaurantRequest(
        @NotBlank(message = "Nome do restaurante é obrigatório")
        String restaurantName,

        @NotBlank(message = "CNPJ do restaurante é obrigatório")
        String cnpj
) {
}
