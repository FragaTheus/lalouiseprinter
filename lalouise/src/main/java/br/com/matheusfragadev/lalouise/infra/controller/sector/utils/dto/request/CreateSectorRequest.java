package br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.request;

import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateSectorRequest(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Descrição é obrigatória")
        String description,

        @NotNull(message = "Tipos de armazenamento são obrigatórios")
        List<Storage> storages
) {
}

