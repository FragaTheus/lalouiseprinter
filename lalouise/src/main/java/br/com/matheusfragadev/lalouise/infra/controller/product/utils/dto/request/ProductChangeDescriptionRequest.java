package br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProductChangeDescriptionRequest(
        @NotBlank(message = "Descrição do produto é obrigatória")
        String newDescription
) {
}
