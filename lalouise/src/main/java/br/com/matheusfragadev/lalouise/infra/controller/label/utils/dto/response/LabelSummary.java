package br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto.response;

import br.com.matheusfragadev.lalouise.domain.label.enums.Status;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record LabelSummary(
        UUID id,
        String productName,
        String sectorName,
        String lot,
        Instant validateDate,
        Status status
) {
}
