package br.com.matheusfragadev.lalouise.application.print.utils.command;

import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;

import java.util.UUID;

public record GeneraleLabelForNewLocationCommand(
        UUID currentLabelId,
        UUID userId,
        Storage storage,
        Integer copies
) {
}

