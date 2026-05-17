package br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto.response;

import br.com.matheusfragadev.lalouise.domain.label.enums.Status;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record LabelInfo(
        UUID id,
        String restaurantName,
        String sectorName,
        String productName,
        String printedBy,
        String lot,
        Instant validateDate,
        Instant createdAt,
        Instant updateAt,
        Status status
) {
}
