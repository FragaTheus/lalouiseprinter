package br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.request;

import br.com.matheusfragadev.lalouise.domain.product.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotNull(message = "Categoria é obrigatória")
        Category category
) {
}

