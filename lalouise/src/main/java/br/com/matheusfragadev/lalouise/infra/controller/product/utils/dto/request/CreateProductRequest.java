package br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateProductRequest(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Descrição é obrigatória")
        String description
) {
}

