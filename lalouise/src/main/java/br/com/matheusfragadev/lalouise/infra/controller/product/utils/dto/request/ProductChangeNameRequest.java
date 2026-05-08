package br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProductChangeNameRequest(
        @NotBlank(message = "Nome do produto é obrigatório")
        String newProductName
) {
}
