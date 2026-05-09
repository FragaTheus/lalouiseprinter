package br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SectorChangeNameRequest(
        @NotBlank(message = "Nome do setor é obrigatório")
        String newName
) {
}

