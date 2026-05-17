package br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto;


import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReprintLabelRequestByInput(
        @NotNull Storage storage,
        @NotNull UUID sectorId
) {
}
