package br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SectorSummary(
        UUID id,
        String name,
        boolean active
) {
}
