package br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SectorChangeDescriptionRequest(
        @NotBlank(message = "Descrição do setor é obrigatória")
        String newDescription
) {
}

