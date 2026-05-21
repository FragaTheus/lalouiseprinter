package br.com.matheusfragadev.lalouise.application.print.utils.command;

import br.com.matheusfragadev.lalouise.domain.label.vo.Lot;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ZplGenerateCommand(
        Lot lot,
        Instant validateDate,
        Instant createdAt,
        String restaurantName,
        String sectorName,
        String productName,
        String printedByName,
        UUID restaurantId,
        UUID labelId
) {
}
