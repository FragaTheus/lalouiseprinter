package br.com.matheusfragadev.lalouise.application.label.utils;

import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;

import java.util.UUID;

public record ReprintLabelByInputCommand(
        UUID currentLabelId,
        UUID userId,
        Storage storage,
        UUID sectorId
) {
}
