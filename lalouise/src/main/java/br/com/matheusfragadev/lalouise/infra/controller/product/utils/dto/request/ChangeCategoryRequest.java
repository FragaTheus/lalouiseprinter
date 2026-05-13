package br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.request;

import br.com.matheusfragadev.lalouise.domain.product.enums.Category;
import jakarta.validation.constraints.NotNull;

public record ChangeCategoryRequest(
        @NotNull(message = "A categoria do produto é obrigatória.")
        Category category
) {
}
