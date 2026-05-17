package br.com.matheusfragadev.lalouise.application.label.utils;

import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PrintLabelCommand(
        UUID productId,
        UUID userId,
        Storage storage
) {
}
