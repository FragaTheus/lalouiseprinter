package br.com.matheusfragadev.lalouise.application.print.utils.command;

import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;

import java.util.UUID;

public record ReprintLabelCommand(
        UUID currentLabelId,
        UUID userId,
        Storage storage,
        int copies
) {
}

