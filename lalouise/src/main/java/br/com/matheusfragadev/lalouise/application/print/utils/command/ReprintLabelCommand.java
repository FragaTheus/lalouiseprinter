package br.com.matheusfragadev.lalouise.application.print.utils.command;

import java.util.UUID;

public record ReprintLabelCommand(
        UUID labelId, UUID userId, Integer copies
) {
}
