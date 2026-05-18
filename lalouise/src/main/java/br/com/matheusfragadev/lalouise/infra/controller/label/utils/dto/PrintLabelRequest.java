package br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto;

import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.UUID;

public record PrintLabelRequest(
        UUID productId,
        Storage storage,
        @Min(1) @Max(99)
        Integer copies
) {
}
