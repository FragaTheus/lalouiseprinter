package br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto;

import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import jakarta.validation.constraints.NotNull;


public record ReprintLabelRequest(
        @NotNull Storage storage
) {
}


