package br.com.matheusfragadev.lalouise.application.print;

import br.com.matheusfragadev.lalouise.domain.label.vo.Lot;

import java.time.Instant;

public record ZplGenerateCommand(
        Lot lot,
        Instant validateDate,
        Instant createdAt,
        String restaurantName,
        String sectorName,
        String productName,
        String printedByName
) {
}
