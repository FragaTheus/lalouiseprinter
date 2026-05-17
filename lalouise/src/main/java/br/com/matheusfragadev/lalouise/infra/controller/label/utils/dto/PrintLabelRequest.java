package br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto;

import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;

import java.util.UUID;

public record PrintLabelRequest(
        UUID productId,
        Storage storage
) {
}
