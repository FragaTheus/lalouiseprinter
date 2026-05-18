package br.com.matheusfragadev.lalouise.infra.controller.label.utils.resolver;

import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import lombok.Builder;

@Builder
public record LabelInfoResolverResult(
        Label label,
        String restaurantName,
        String sectorName,
        String productName,
        String printedByName
) {
}
